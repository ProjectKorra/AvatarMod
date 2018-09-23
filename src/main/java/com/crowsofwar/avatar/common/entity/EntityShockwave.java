package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityShockwave extends AvatarEntity {

	public EnumParticleTypes particle;
	//Particles to be spawned.
	private int particleAmount;
	//The amount of particles to be spawned
	private double particleSpeed;
	//Speed of the particles


	private int performanceAmount;
	//The amount of battleperformance added per hit
	private double range;
	//The range of the shockwave/how far it'll go before dissipating
	public double speed;
	//The speed of the shockwave and how fast entities will be knocked back
	private double knockbackHeight;
	//The amount entities will be knocked back
	public float damage;
	//The amount of damage the shockwave will do
	private boolean isFire;
	//Whether or not to set the target entities on fire
	private int fireTime;
	//How long to set the target entity on fire
	private boolean isSphere;
	//Whether or not to use a sphere of particles instead of a circular ring
	private NetworkParticleSpawner particles;

	private double particleController;
	//Used for spherical shockwaves

	public void setParticle(EnumParticleTypes particle) {
		this.particle = particle;
	}

	public void setParticleAmount(int amount) {
		this.particleAmount = amount;
	}

	public void setParticleSpeed(double speed) {
		this.particleSpeed = speed;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setKnockbackHeight(double height) {
		this.knockbackHeight = height;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public void setFire(boolean fire) {
		this.isFire = fire;
	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	public void setPerformanceAmount(int amount) {
		this.performanceAmount = amount;
	}

	public void setSphere(boolean sphere) {
		this.isSphere = sphere;
	}

	public void setParticleController(double amount) {
		this.particleController = amount;
	}

	public EnumParticleTypes getParticle() {
		return particle;
	}

	public int getParticleAmount() {
		return particleAmount;
	}

	public double getParticleSpeed() {
		return particleSpeed;
	}

	public double getRange() {
		return range;
	}

	public double getSpeed() {
		return speed;
	}

	public double getKnockbackHeight() {
		return knockbackHeight;
	}

	public double getDamage() {
		return damage;
	}

	public boolean getSphere() {
		return isSphere;
	}






	public EntityShockwave(World world){
		super(world);
		this.damage = 1;
		this.particle = EnumParticleTypes.CLOUD;
		this.particleSpeed = 0;
		this.particleAmount = 10;
		this.range = 4;
		this.performanceAmount = 10;
		this.knockbackHeight = 0.2;
		this.speed = 0.8;
		this.isFire = false;
		this.fireTime = 0;
		this.particleAmount = 0;
		this.setSize(1, 1);
		this.particles = new NetworkParticleSpawner();
	}

	@Override
	public void onUpdate() {

		this.setVelocity(Vector.ZERO);
		if (getOwner() != null) {
			this.setPosition(getOwner().posX, getOwner().getEntityBoundingBox().minY, getOwner().posZ);
		}

		if (!world.isRemote) {

			if (ticksExisted * speed > range) {
				this.setDead();
			}


			for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (getRange() * 10 * 1.5)) {
				double x = posX + (ticksExisted * getSpeed()) * Math.sin(angle);
				double y = posY + 0.5;
				double z = posZ + (ticksExisted * getSpeed()) * Math.cos(angle);
				particles.spawnParticles(world, getParticle(), getParticleAmount() / 2, getParticleAmount(), x, y, z, getParticleSpeed(),
						getParticleSpeed(), getParticleSpeed());
			}

			if (isSphere) {
				double x, y, z;
				if (ticksExisted % 3 == 0) {
					for (double theta = 0; theta <= 180; theta += 1) {
						double dphi = particleController / Math.sin(Math.toRadians(theta));

						for (double phi = 0; phi < 360; phi += dphi) {
							double rphi = Math.toRadians(phi);
							double rtheta = Math.toRadians(theta);

							x = ticksExisted * getSpeed() * Math.cos(rphi) * Math.sin(rtheta);
							y = ticksExisted * getSpeed() * Math.sin(rphi) * Math.sin(rtheta);
							z = ticksExisted * getSpeed() * Math.cos(rtheta);

							particles.spawnParticles(world, EnumParticleTypes.EXPLOSION_NORMAL, getParticleAmount() / 2, getParticleAmount(), x + posX, y + posY,
									z + posZ, getParticleSpeed(), getParticleSpeed(), getParticleSpeed());

						}
					}//Creates a sphere. Courtesy of Project Korra's Air Burst!
				}
			}
		}

			AxisAlignedBB box = new AxisAlignedBB(posX + (ticksExisted * speed), posY + 1.5, posZ + (ticksExisted * speed),
					posX - (ticksExisted * speed), posY - 1.5, posZ - (ticksExisted * speed));

			List<Entity> targets = world.getEntitiesWithinAABB(
					Entity.class, box);

			targets.remove(getOwner());

			for (Entity target : targets) {
				if (target != getOwner() && this.canCollideWith(target) && target != this && !(target instanceof EntityItem)) {

						if (this.canDamageEntity(target)) {
							target.attackEntityFrom(
									AvatarDamageSource.causeAirDamage(target, this.getOwner()),
									damage);
							BattlePerformanceScore.addScore(getOwner(), performanceAmount);
							target.setFire(isFire ? fireTime : 0);
						}
						if (getAbility() != null && !world.isRemote && getAbility() instanceof AbilityAirBurst) {
							AbilityData aD = AbilityData.get(getOwner(), getAbility().getName());
							aD.addXp(SKILLS_CONFIG.airBurstHit-aD.getLevel());
							if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
								((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 50));
								((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 50));
								((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 50));
							}
						}
						target.motionX += Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().x() * (ticksExisted/20F * speed);
						target.motionY += isSphere ? Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().y() * (ticksExisted/20F * speed) : knockbackHeight; // Throws target into the air.
						target.motionZ += Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().z() * (ticksExisted/20F * speed);

						AvatarUtils.afterVelocityAdded(target);
				}
			}
		}
	}

