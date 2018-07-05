package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
			(EntityWaterCannon.class, DataSerializers.FLOAT);



	private float damage;

	public EntityWaterCannon(World world) {
		super(world);
		setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());
		damage = 0.5F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE, 1.5f);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world instanceof WorldServer) {
			WorldServer World = (WorldServer) world;
			World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX, posY, posZ, 4, 0.1, 0.1, 0.1, 0.005);
			World.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY, posZ, 200, 0.1, 0.1, 0.1, 0.03);
		}
		if (getOwner() != null) {
			Vector direction = Vector.getLookRectangular(getOwner());
			this.setVelocity(direction.times(20));
		}


		if (ticksExisted >= 125 && !world.isRemote) {
			setDead();
		}

		setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());

	}

	@Override
	protected void updateCpBehavior() {

		// First control point (at front) should just follow water cannon
		getControlPoint(0).setPosition(Vector.getEntityPos(this));


		// Second control point (at back) should stay near the player
		if (getOwner() != null) {
			Vector eyePos = Vector.getEyePos(getOwner());
			Vector directionToEnd = position().minus(eyePos).normalize();
			getControlPoint(1).setPosition(eyePos.plus(directionToEnd.times(0.5)));
		}

	}

	@Override
	protected void onCollideWithEntity(Entity entity) {

			damageEntity((EntityLivingBase) entity, 1);
			world.playSound(null, getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH,
					SoundCategory.PLAYERS, 1, 1);

	}

	/**
	 * Custom water cannon collision detection which uses raytrace. Required since water cannon moves
	 * quickly and can sometimes "glitch" through an entity without detecting the collision.
	 */
	@Override
	protected void collideWithNearbyEntities() {

		List<Entity> collisions = Raytrace.entityRaytrace(world, position(), velocity(), velocity
				().magnitude() / 20, entity -> entity != getOwner() && entity != this);

		for (Entity collided : collisions) {
			onCollideWithEntity(collided);
		}

	}

	private void damageEntity(EntityLivingBase entity, float damageModifier) {

		if (world.isRemote) {
			return;
		}

		DamageSource damageSource = AvatarDamageSource.causeWaterCannonDamage(entity, getOwner());
		if (entity.attackEntityFrom(damageSource, damage *
				damageModifier)) {

			BattlePerformanceScore.addLargeScore(getOwner());

			entity.motionY = this.motionY;
			entity.motionZ = this.motionZ;
			entity.motionX = this.motionX;
			AvatarUtils.afterVelocityAdded(entity);

			// Add Experience
			// Although 2 water cannon entities are fired in each water cannon ability, this won't
			// cause 2x XP rewards as this only happens when the entity is successfully attacked
			// (hurtResistantTime prevents the 2 water cannon entities from both damaging at once)
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("water_cannon");
				abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
			}
		}

	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return entity != getOwner() || entity instanceof EntityLivingBase;
	}

	@Override
	public boolean onCollideWithSolid() {
		setVelocity(Vector.ZERO);
		return false;
	}

	@Override
	protected EntityWaterCannon.CannonControlPoint createControlPoint(float size, int index) {
		return new EntityWaterCannon.CannonControlPoint(this, index);
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

	public class CannonControlPoint extends ControlPoint {

		public CannonControlPoint(EntityArc arc, int index) {
			// Make all control points the same size
			super(arc, index == 1 ? 0.75f : 0.75f, 0, 0, 0);
		}

	}
}


