package com.crowsofwar.avatar.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.util.*;
import com.crowsofwar.gorecore.util.Vector;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityWaterCannon.class, DataSerializers.FLOAT);

	private float damage;
	private float lifeTime;

	public EntityWaterCannon(World world) {
		super(world);
		setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());
		damage = 0.5F;
		putsOutFires = true;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getSizeMultiplier() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSizeMultiplier(float sizeMultiplier) {
		dataManager.set(SYNC_SIZE, sizeMultiplier);
	}

	public void setLifeTime(float ticks) {
		lifeTime = ticks;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE, 1f);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.PLAYERS, 1, 2);
		int numberOfParticles = (int) (500 * getSizeMultiplier());

		if (!world.isRemote && collided) {
			WorldServer World = (WorldServer) world;
			World.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, posY, posZ, numberOfParticles, 0, 0, 0, 0.05 + getSizeMultiplier() / 10);
		}

		if (getOwner() != null) {
			Vector direction = Vector.getLookRectangular(getOwner());
			setVelocity(direction.times(20));
			double x = getOwner().posX - posX;
			double y = getOwner().posY - posY;
			double z = getOwner().posZ - posZ;
			rotationYaw = (float) (MathHelper.atan2(x, z) * (180 / Math.PI));
			rotationPitch = (float) (MathHelper.atan2(y, MathHelper.sqrt(x * x + z * z)) * (180 / Math.PI));
		}

		if (ticksExisted >= lifeTime && !world.isRemote) {
			setDead();

		}

		if (getOwner() == null) {
			setDead();
		}

		setSize(1.5F * getSizeMultiplier(), 1.5F * getSizeMultiplier());

	}

	@Override
	protected void updateCpBehavior() {

		// First control point (at front) should just follow water cannon
		getControlPoint(0).setPosition(Vector.getEntityPos(this).plusY(getSizeMultiplier()));
		//The control point's top gets set to the water cannon; you want the center to be set
		//to the water cannon.

		// Second control point (at back) should stay near the player
		if (getOwner() != null) {
			Vector eyePos = Vector.getEyePos(getOwner());
			Vector directionToEnd = position().minus(eyePos).normalize();
			getControlPoint(1).setPosition(eyePos.plus(directionToEnd.times(0.5)));
		}

	}

	@Override
	protected void onCollideWithEntity(Entity entity) {

		if (!world.isRemote) {
			int numberOfParticles = (int) (500 * getSizeMultiplier());
			WorldServer World = (WorldServer) world;
			World.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, posY, posZ, numberOfParticles, 0, 0, 0, 0.05 + getSizeMultiplier() / 10);
			//Change based on size
		}

		damageEntity((EntityLivingBase) entity);
		world.playSound(null, getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1, 1);

	}

	/**
	 * Custom water cannon collision detection which uses raytrace. Required since water cannon moves
	 * quickly and can sometimes "glitch" through an entity without detecting the collision.
	 * That's because the hitbox is wonky, and also because the hitbox is well, a box, at the end of the water cannon. If
	 * it were to extend across the entire entity.... Well let's just say that minecraft
	 * wouldn't be happy.
	 */
	@Override
	protected void collideWithNearbyEntities() {
		//Gonna try something crazy.

		if (getOwner() != null) {
			BendingData data = BendingData.get(getOwner());

			List<Entity> collisions = Raytrace.entityRaytrace(world, getControlPoint(1).position(), velocity(), velocity().magnitude() / 20,
															  entity -> entity != getOwner() && entity != this);
			/*Original raytrace- but, it's pretty glitchy. Basically, look at an entity, and the water cannon will teleport.
			That's why you have to use this complex vector maths to get the water cannon to face the player.**/
			/*double dist = this.getDistanceToEntity(getOwner());
			Vec3d direction = Vec3d.fromPitchYaw(rotationPitch, rotationYaw);

			List<Entity> collisions = Raytrace.entityRaytrace(world, getControlPoint(0).position(),Vector.getLookRectangular(this), dist, entity -> entity != getOwner());
			**/
			if (!collisions.isEmpty()) {
				for (Entity collided : collisions) {
					if (canCollideWith(collided)) {
						onCollideWithEntity(collided);
						//Needed because the water cannon will still glitch through the entity
						if (!(data.getAbilityData("water_cannon").isMasterPath(AbilityData.AbilityTreePath.SECOND))) {
							setPosition(collided.posX, collided.posY + (collided.getEyeHeight() / 2), collided.posZ);
						}
					}
				}
			}
		}
	}

	private void damageEntity(EntityLivingBase entity) {

		if (world.isRemote) {
			return;
		}

		DamageSource damageSource = AvatarDamageSource.causeWaterCannonDamage(entity, getOwner());
		if (entity.attackEntityFrom(damageSource, damage)) {

			BattlePerformanceScore.addSmallScore(getOwner());

			entity.motionX = motionX * 2;
			entity.motionY = motionY * 2;
			entity.motionZ = motionZ * 2;
			AvatarUtils.afterVelocityAdded(entity);

			// Add Experience
			// Although 2 water cannon entities are fired in each water cannon ability, this won't
			// cause 2x XP rewards as this only happens when the entity is successfully attacked
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("water_cannon");
				abilityData.addXp(SKILLS_CONFIG.waterHit / 2);
			}
		}

	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof AvatarEntity) {
			if (((AvatarEntity) entity).getOwner() == getOwner()) {
				return false;
			}
		}
		return entity != getOwner() && !(entity instanceof EntityItem) && !(entity instanceof EntityWaterCannon)
						&& entity instanceof EntityLivingBase;
	}

	@Override
	public boolean onCollideWithSolid() {
		setVelocity(Vector.ZERO);
		onMajorWaterContact();
		return false;
	}

	@Override
	protected EntityWaterCannon.CannonControlPoint createControlPoint(float size, int index) {
		return new EntityWaterCannon.CannonControlPoint(this, index);
	}

	public class CannonControlPoint extends ControlPoint {

		public CannonControlPoint(EntityArc arc, int index) {
			// Make all control points the same size
			super(arc, index == 1 ? 0.75f : 0.75f, 0, 0, 0);
		}

	}
}


