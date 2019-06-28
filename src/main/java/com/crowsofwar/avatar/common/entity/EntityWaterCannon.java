package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterCannon;
import com.crowsofwar.avatar.common.bending.water.WaterChargeHandler;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityWaterCannon extends EntityArc<EntityWaterCannon.CannonControlPoint> {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
			(EntityWaterCannon.class, DataSerializers.FLOAT);


	private float damage;
	private float lifeTime;
	private ParticleSpawner particles;
	private double maxRange;
	private double range;
	private Vec3d knockBack;

	public EntityWaterCannon(World world) {
		super(world);
		setSize(1.5f * getSizeMultiplier(), 1.5f * getSizeMultiplier());
		damage = 0.5F;
		this.putsOutFires = true;
		this.noClip = false;
		this.particles = new NetworkParticleSpawner();
		this.setInvisible(false);
		this.range = 0;
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
		this.lifeTime = ticks;
	}

	public void setMaxRange(float range) {
		this.maxRange = range;
	}

	public void setKnockBack(Vec3d knockBack) {
		this.knockBack = knockBack;
	}


	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE, 1f);
		range = 0;
	}

	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
				SoundCategory.PLAYERS, 1, 2);


		if (getOwner() != null) {
			if (getAbility() instanceof AbilityWaterCannon) {
				Vec3d startPos = getControlPoint(getAmountOfControlPoints() - 1).position().toMinecraft();
				Vec3d distance = getOwner().getLookVec().scale(range);
				Vec3d endPos = startPos.add(distance);
				range += range < maxRange ? maxRange / lifeTime : 0;

				Vec3d speed = endPos.subtract(startPos);

				if (!world.isRemote) {
					if (onCollideWithSolid()) {
						this.motionX = this.motionY = this.motionZ = 0;
						setVelocity(Vector.ZERO);
					} else {
						this.motionX = speed.x / 60;
						this.motionY = speed.y / 60;
						this.motionZ = speed.z / 60;
					}
				}

			}

			if (ticksExisted % 4 == 0 && !this.isDead && STATS_CONFIG.waterCannonSettings.useWaterCannonParticles) {
				double dist = this.getDistance(getOwner());
				int particleController = 20;
				if (getAbility() instanceof AbilityWaterCannon && !world.isRemote) {
					AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
					particleController = 23;
					if (data.getLevel() == 1) {
						particleController = 20;
					}
					if (data.getLevel() >= 2) {
						particleController = 17;
					}

					if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
						particleController = 120;
					}
				}
				for (double i = 0; i < 1; i += 1 / dist) {
					Vector startPos = getControlPoint(getAmountOfControlPoints() - 1).position();
					Vector distance = this.position().minus(getControlPoint(getAmountOfControlPoints() - 1).position());
					distance = distance.times(i);
					for (double angle = 0; angle < 360; angle += particleController) {
						Vector position = Vector.getOrthogonalVector(this.position().minus(getControlPoint(getAmountOfControlPoints() - 1).position()), angle, getSizeMultiplier() * 1.4);
						particles.spawnParticles(world, EnumParticleTypes.WATER_WAKE, 1, 1,
								position.x() + startPos.x() + distance.x(), position.y() + startPos.y() + distance.y(), position.z() + startPos.z() + distance.z(), 0, 0, 0);

					}
					particles.spawnParticles(world, EnumParticleTypes.WATER_WAKE, 1, 1,
							startPos.x() + distance.x(), startPos.y() + distance.y(), startPos.z() + distance.z(), 0, 0, 0);
				}

			}
		}


		if (this.ticksExisted >= lifeTime && !world.isRemote) {
			setDead();
		}
		if (ticksExisted > 150) {
			setDead();
		}

		if (getOwner() == null) {
			setDead();
		}


		setSize(1.5F * getSizeMultiplier(), 1.5F * getSizeMultiplier());

	}


	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	protected double getControlPointMaxDistanceSq() {
		return 0.05;
	}

	@Override
	protected double getControlPointTeleportDistanceSq() {
		return 0.15;
	}

	@Override
	protected void updateCpBehavior() {

		super.updateCpBehavior();
		// First control point (at front) should just follow water cannon
		//getControlPoint(0).setPosition(Vector.getEntityPos(this).plusY(getSizeMultiplier()));
		//The control point's top gets set to the water cannon; you want the center to be set
		//to the water cannon.


		// Last control point (at back) should stay near the player
		if (getOwner() != null) {
			Vector eyePos = Vector.getEyePos(getOwner()).minus(0, 0.3, 0);
			Vector directionToEnd = position().minus(eyePos).normalize();
			getControlPoint(getAmountOfControlPoints() - 1).setPosition(eyePos.plus(directionToEnd.times(0.075)));
		}

	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (this.canCollideWith(entity) && getOwner() != entity) {


			if (!world.isRemote) {
				int numberOfParticles = (int) (400 * getSizeMultiplier());
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

		if (getOwner() != null) {
			BendingData data = BendingData.get(getOwner());


			double dist = this.getDistance(getOwner());
			List<Entity> collisions = Raytrace.entityRaytrace(world, getControlPoint(getAmountOfControlPoints() - 1).position(), this.position().minus(getControlPoint(getAmountOfControlPoints() - 1).position()), dist, entity -> entity != getOwner());

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

				entity.motionX = knockBack.x;
				entity.motionY = knockBack.y;
				entity.motionZ = knockBack.z;
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
		if (getOwner() != null && !world.isRemote) {
			Raytrace.Result result = Raytrace.getTargetBlock(getOwner(), maxRange);
			if (result.getPos() != null && result.getPosPrecise() != null && result.getPos().toBlockPos() == getPosition()) {
				setPosition(result.getPosPrecise());
				posX = result.getPosPrecise().x();
				posY = result.getPosPrecise().y();
				posZ = result.getPosPrecise().z();
				this.motionX = this.motionY = this.motionZ = 0;
				return true;
			}
		}
		return false;
	}

	@Override
	public EntityWaterCannon.CannonControlPoint createControlPoint(float size, int index) {
		return new EntityWaterCannon.CannonControlPoint(this, index);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected double getVelocityMultiplier() {
		return 14;
	}

	@Override
	public void setDead() {
		super.setDead();
		if (getOwner() != null) {
			AttributeModifier modifier = getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WaterChargeHandler.MOVEMENT_MODIFIER_ID);
			if (modifier != null) {
				getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
			}
		}
	}

	class CannonControlPoint extends ControlPoint {

		private CannonControlPoint(EntityArc arc, int index) {
			// Make all control points the same size
			super(arc, index == 0 ? 0.5f : 0.5F - 0.15F * (index / 10F), 0, 0, 0);
		}

	}
}


