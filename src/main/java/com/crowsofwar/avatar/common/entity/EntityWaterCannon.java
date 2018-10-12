package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
			(EntityWaterCannon.class, DataSerializers.FLOAT);


	private float damage;
	private float lifeTime;
	private ParticleSpawner particles;

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
		this.lifeTime = ticks;
	}

	public EntityWaterCannon(World world) {
		super(world);
		setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());
		damage = 0.5F;
		this.putsOutFires = true;
		this.noClip = false;
		this.particles = new NetworkParticleSpawner();
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
		if (getOwner() == null) setDead();

		world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
				SoundCategory.PLAYERS, 1, 2);

		if (ticksExisted % 2 == 0) {
			double dist = this.getDistance(getOwner());
			for (double i = 0; i < 1; i += 1 / dist) {
				for (double angle = 0; angle < 360; angle += 5/getSizeMultiplier()) {
					Vector position = AvatarUtils.getOrthogonalVector(velocity().normalize(), angle, getSizeMultiplier() * 1.5);
					Vector startPos = getControlPoint(1).position();
					Vector distance = this.position().minus(getControlPoint(1).position());
					distance = distance.times(i);
					particles.spawnParticles(world, EnumParticleTypes.WATER_WAKE, 1, 2,
							position.x() + startPos.x() + distance.x(), position.y() + startPos.y() + distance.y(), position.z() + startPos.z() + distance.z(), 0, 0, 0);
				}
			}
		}


		if (getOwner() != null) {
			Vector direction = Vector.getLookRectangular(getOwner());
			this.setVelocity(direction.times(20));
		}



		if (this.ticksExisted >= lifeTime && !world.isRemote) {
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
			Vector eyePos = Vector.getEyePos(getOwner()).minus(0, 0.3, 0);
			Vector directionToEnd = position().minus(eyePos).normalize();
			getControlPoint(1).setPosition(eyePos.plus(directionToEnd.times(0.5)));
		}

	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (this.canCollideWith(entity) && getOwner() != entity) {


			if (!world.isRemote) {
				int numberOfParticles = (int) (500 * getSizeMultiplier());
				WorldServer World = (WorldServer) world;
				World.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, posY, posZ, numberOfParticles, 0, 0, 0, 0.05 + getSizeMultiplier() / 10);
				//Change based on size
			}

			damageEntity(entity);
			world.playSound(null, getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH,
					SoundCategory.PLAYERS, 1, 1);

		}
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


			double dist = this.getDistance(getOwner());
			List<Entity> collisions = Raytrace.entityRaytrace(world, getControlPoint(1).position(), this.position().minus(getControlPoint(1).position()), dist, entity -> entity != getOwner());

			if (!collisions.isEmpty()) {
				for (Entity collided : collisions) {
					if (canCollideWith(collided) && collided != getOwner()) {
						onCollideWithEntity(collided);
						//Needed because the water cannon will still glitch through the entity
						if (!(data.getAbilityData("water_cannon").isMasterPath(AbilityData.AbilityTreePath.SECOND))) {
							this.setPosition(collided.posX, this.posY, collided.posZ);
						}
					}
				}
			}
		}
	}

	private void damageEntity(Entity entity) {

		if (world.isRemote) {
			return;
		}

		if (this.canDamageEntity(entity)) {
			DamageSource damageSource = AvatarDamageSource.causeWaterCannonDamage(entity, getOwner());
			if (entity.attackEntityFrom(damageSource, damage)) {

				BattlePerformanceScore.addSmallScore(getOwner());

				entity.motionX = this.motionX * 2;
				entity.motionY = this.motionY * 2;
				entity.motionZ = this.motionZ * 2;
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
	}

	@Override
	public boolean onCollideWithSolid() {
		this.motionX = this.motionY = this.motionZ = 0;
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
			super(arc, index == 1 ? 0.5f : 0.5f, 0, 0, 0);
		}

	}
}


