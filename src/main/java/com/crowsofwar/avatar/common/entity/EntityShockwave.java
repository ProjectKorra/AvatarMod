package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.bending.BattlePerformanceScore.SCORE_MOD_MEDIUM;
import static com.crowsofwar.avatar.common.bending.BattlePerformanceScore.SCORE_MOD_SMALL;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityShockwave extends AvatarEntity {

	private static final DataParameter<String> SYNC_PARTICLE = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.STRING);
	//Name of the particles to be spawned.
	private static final DataParameter<Float> SYNC_PARTICLE_SPEED = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.FLOAT);
	//Speed of the particles
	private static final DataParameter<Integer> SYNC_PARTICLE_AMOUNT = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.VARINT);
	//The amount of particles to be spawned
	private static final DataParameter<Float> SYNC_PARTICLE_CONTROLLER = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.FLOAT);
	//Used for spherical shockwaves
	private static final DataParameter<Float> SYNC_SPEED = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.FLOAT);
	//The speed of the shockwave and how fast entities will be knocked back
	private static final DataParameter<Boolean> SYNC_IS_SPHERE = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.BOOLEAN);
	//Whether or not to use a sphere of particles instead of a circular ring
	private static final DataParameter<Float> SYNC_RANGE = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.FLOAT);
	//The range of the shockwave/how far it'll go before dissipating

	public float damage;
	//The amount of damage the shockwave will do
	private int performanceAmount;
	//The amount of performance added per hit
	private double knockbackHeight;
	//The amount entities will be knocked back
	private int fireTime;
	//How long to set the target entity on fire


	public EntityShockwave(World world) {
		super(world);
		this.damage = 1;
		this.performanceAmount = 10;
		this.knockbackHeight = 0.2;
		this.fireTime = 0;
		this.setSize(1, 1);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_PARTICLE, "cloud");
		dataManager.register(SYNC_PARTICLE_SPEED, 0.1F);
		dataManager.register(SYNC_PARTICLE_AMOUNT, 100);
		dataManager.register(SYNC_PARTICLE_CONTROLLER, 40F);
		dataManager.register(SYNC_SPEED, 0.8F);
		dataManager.register(SYNC_IS_SPHERE, false);
		dataManager.register(SYNC_RANGE, 4F);

	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	public void setPerformanceAmount(int amount) {
		this.performanceAmount = amount;
	}

	public float getParticleController() {
		return dataManager.get(SYNC_PARTICLE_CONTROLLER);
	}

	public void setParticleController(float amount) {
		dataManager.set(SYNC_PARTICLE_CONTROLLER, amount);
	}

	public String getParticleName() {
		return dataManager.get(SYNC_PARTICLE);
	}

	public void setParticleName(String particle) {
		dataManager.set(SYNC_PARTICLE, particle);
	}

	public int getParticleAmount() {
		return dataManager.get(SYNC_PARTICLE_AMOUNT);
	}

	public void setParticleAmount(int amount) {
		dataManager.set(SYNC_PARTICLE_AMOUNT, amount);
	}

	public double getParticleSpeed() {
		return dataManager.get(SYNC_PARTICLE_SPEED);
	}

	public void setParticleSpeed(float speed) {
		dataManager.set(SYNC_PARTICLE_SPEED, speed);
	}

	public double getRange() {
		return dataManager.get(SYNC_RANGE);
	}

	public void setRange(float range) {
		dataManager.set(SYNC_RANGE, range);
	}

	public double getSpeed() {
		return dataManager.get(SYNC_SPEED);
	}

	public void setSpeed(float speed) {
		dataManager.set(SYNC_SPEED, speed);
	}

	public double getKnockbackHeight() {
		return knockbackHeight;
	}

	public void setKnockbackHeight(double height) {
		this.knockbackHeight = height;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public boolean getSphere() {
		return dataManager.get(SYNC_IS_SPHERE);
	}

	public void setSphere(boolean sphere) {
		dataManager.set(SYNC_IS_SPHERE, sphere);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void onUpdate() {

		this.motionX = this.motionY = this.motionZ = 0;

		if ((this.ticksExisted * getSpeed()) > getRange()) {
			this.setDead();
		}
		if (ticksExisted > 140) {
			setDead();
		}

		if (!world.isRemote) {
/*
			for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (getRange() * 10 * 1.5)) {
				double x = posX + (ticksExisted * getSpeed()) * Math.sin(angle);
				double y = posY + 0.5;
				double z = posZ + (ticksExisted * getSpeed()) * Math.cos(angle);
				Vector speed = new Vector((ticksExisted * getSpeed()) * Math.sin(angle) * (getParticleSpeed() * 10), getParticleSpeed() / 2, (ticksExisted * getSpeed()) * Math.cos(angle) * (getParticleSpeed() * 10));
				particles.spawnParticles(world, getParticle(), getParticleAmount() / 2, getParticleAmount(), new Vector(x, y, z), speed);
			}

			if (isSphere) {
				double x, y, z;
				if (ticksExisted % 2 == 0) {
					for (double theta = 0; theta <= 180; theta += 1) {
						double dphi = particleController / Math.sin(Math.toRadians(theta));

						for (double phi = 0; phi < 360; phi += dphi) {
							double rphi = Math.toRadians(phi);
							double rtheta = Math.toRadians(theta);

							x = ticksExisted * getSpeed() * Math.cos(rphi) * Math.sin(rtheta);
							y = ticksExisted * getSpeed() * Math.sin(rphi) * Math.sin(rtheta);
							z = ticksExisted * getSpeed() * Math.cos(rtheta);

							particles.spawnParticles(world, getParticle(), getParticleAmount() / 2, getParticleAmount(), x + posX, y + posY,
									z + posZ, getParticleSpeed(), getParticleSpeed(), getParticleSpeed());

						}
					}//Creates a sphere. Courtesy of Project Korra's Air Burst!
				}
			}**/
		}

		if (!world.isRemote) {
			AxisAlignedBB box = new AxisAlignedBB(posX + (ticksExisted * getSpeed()), posY + 1.5, posZ + (ticksExisted * getSpeed()),
					posX - (ticksExisted * getSpeed()), posY - 1.5, posZ - (ticksExisted * getSpeed()));

			List<Entity> targets = world.getEntitiesWithinAABB(
					Entity.class, box);

			targets.remove(getOwner());

			if (!targets.isEmpty()) {
				for (Entity target : targets) {
					if (this.canCollideWith(target) && target != this) {

						if (this.canDamageEntity(target) && !world.isRemote) {
							if (target.attackEntityFrom(AvatarDamageSource.causeShockwaveDamage(target, getOwner()), damage)) {
								int amount = performanceAmount > SCORE_MOD_SMALL ? performanceAmount : (int) SCORE_MOD_SMALL;
								amount = amount > SCORE_MOD_MEDIUM ? (int) SCORE_MOD_MEDIUM : performanceAmount;
								BattlePerformanceScore.addScore(getOwner(), amount);
								target.setFire(fireTime);
								if (getAbility() != null && getAbility() instanceof AbilityAirBurst) {
									AbilityData aD = AbilityData.get(getOwner(), getAbility().getName());
									aD.addXp(SKILLS_CONFIG.airBurstHit - aD.getLevel());
									if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST) && target instanceof EntityLivingBase) {
										((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 50));
										((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 50));
										((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 50));
									}
								}
							}
						}
						double xSpeed = getSphere() ? Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().x() * (ticksExisted * getSpeed()) :
								Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().x() * (ticksExisted / 5F * getSpeed());
						double ySpeed = getSphere() ? Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().y() * (ticksExisted / 2F * getSpeed()) :
								knockbackHeight; // Throws target into the air.
						double zSpeed = getSphere() ? Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().z() * (ticksExisted * getSpeed()) :
								Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().z() * (ticksExisted / 5F * getSpeed());
						ySpeed = ySpeed > knockbackHeight ? ySpeed : knockbackHeight;
						target.motionX += xSpeed;
						target.motionY += ySpeed * 2;
						target.motionZ += zSpeed;

						AvatarUtils.afterVelocityAdded(target);
					}
				}
			}
		}
	}
}

