package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.entity.data.ShockwaveBehaviour;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.bending.BattlePerformanceScore.SCORE_MOD_MEDIUM;
import static com.crowsofwar.avatar.common.bending.BattlePerformanceScore.SCORE_MOD_SMALL;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityShockwave extends AvatarEntity {

	private static final DataParameter<ShockwaveBehaviour> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityShockwave.class, ShockwaveBehaviour.DATA_SERIALIZER);

	private static final DataParameter<String> SYNC_PARTICLE = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.STRING);
	//Name of the particles to be spawned.
	private static final DataParameter<Float> SYNC_PARTICLE_SPEED = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.FLOAT);
	//Speed of the particles
	private static final DataParameter<Integer> SYNC_PARTICLE_AMOUNT = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.VARINT);
	//The amount of particles to be spawned
	private static final DataParameter<Integer> SYNC_PARTICLE_WAVES = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.VARINT);
	//Waves of particles to be spawned
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
	//The minimum height entities will be knocked back. Set to 0 for no effect.
	private int fireTime;
	//How long to set the target entity on fire
	private DamageSource source;

	private Vec3d knockbackMult;
	//Individual values for how to scale the knockback


	public EntityShockwave(World world) {
		super(world);
		this.damage = 1;
		this.performanceAmount = 10;
		this.knockbackHeight = 0;
		this.fireTime = 0;
		this.source = AvatarDamageSource.AIR;
		this.knockbackMult = new Vec3d(1, 2, 1);
		this.setSize(1, 1);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_PARTICLE, "cloud");
		dataManager.register(SYNC_PARTICLE_SPEED, 0.1F);
		dataManager.register(SYNC_PARTICLE_AMOUNT, 1);
		dataManager.register(SYNC_PARTICLE_WAVES, 1);
		dataManager.register(SYNC_PARTICLE_CONTROLLER, 40F);
		dataManager.register(SYNC_SPEED, 0.8F);
		dataManager.register(SYNC_IS_SPHERE, false);
		dataManager.register(SYNC_RANGE, 4F);
		dataManager.register(SYNC_BEHAVIOR, new ShockwaveBehaviour.Idle());

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

	public EnumParticleTypes getParticle() {
		return EnumParticleTypes.getByName(dataManager.get(SYNC_PARTICLE)) != null ? EnumParticleTypes.getByName(dataManager.get(SYNC_PARTICLE)) :
				AvatarParticles.getParticleFromName(dataManager.get(SYNC_PARTICLE));
	}

	public void setParticle(EnumParticleTypes particle) {
		dataManager.set(SYNC_PARTICLE, particle.getParticleName());
	}

	public void setParticleWaves(int waves) {
		dataManager.set(SYNC_PARTICLE_WAVES, waves);
	}

	public int getParticleWaves() {
		return dataManager.get(SYNC_PARTICLE_WAVES);
	}

	public int getParticleAmount() {
		return dataManager.get(SYNC_PARTICLE_AMOUNT);
	}

	public void setParticleAmount(int amount) {
		dataManager.set(SYNC_PARTICLE_AMOUNT, amount);
	}

	public void setBehaviour(ShockwaveBehaviour behaviour) {
		dataManager.set(SYNC_BEHAVIOR, behaviour);
	}

	public ShockwaveBehaviour getBehaviour() {
		return dataManager.get(SYNC_BEHAVIOR);
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

	public void setKnockbackMult(Vec3d mult) {
		this.knockbackMult = mult;
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

	public void setDamageSource(DamageSource source) {
		this.source = source;
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
		super.onUpdate();
		setBehaviour((ShockwaveBehaviour) getBehaviour().onUpdate(this));

		this.motionX = this.motionY = this.motionZ = 0;

		if ((this.ticksExisted * getSpeed()) > getRange()) {
			this.setDead();
		}
		if (ticksExisted > 140) {
			setDead();
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
						if (this.canDamageEntity(target)) {
							DamageSource ds = getSphere() ? AvatarDamageSource.causeSphericalShockwaveDamage(target, getOwner(), source) : AvatarDamageSource.causeShockwaveDamage(target, getOwner(), source);
							if (target.attackEntityFrom(ds, damage)) {
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
						double ySpeed = Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().y() * (ticksExisted / 2F * getSpeed()); // Throws target into the air.
						double zSpeed = getSphere() ? Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().z() * (ticksExisted * getSpeed()) :
								Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().z() * (ticksExisted / 5F * getSpeed());
						if (knockbackHeight != 0) {
							ySpeed = ySpeed > knockbackHeight ? ySpeed : knockbackHeight;
						}
						xSpeed *= knockbackMult.x;
						ySpeed *= knockbackMult.y;
						zSpeed *= knockbackMult.z;
						target.motionX += xSpeed;
						target.motionY += ySpeed;
						target.motionZ += zSpeed;

						AvatarUtils.afterVelocityAdded(target);
					}
				}
			}
		}
	}
}

