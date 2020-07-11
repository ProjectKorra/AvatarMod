package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.entity.data.ShockwaveBehaviour;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityShockwave extends EntityOffensive {

	//TODO: A way to put out fires??

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
	private static final DataParameter<Boolean> SYNC_RENDER_NORMAL = EntityDataManager.createKey(EntityShockwave.class, DataSerializers.BOOLEAN);
	//Whether you want to use the shockwave render class or use the ParticleBuilder system in your behaviour class.

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
		dataManager.register(SYNC_RENDER_NORMAL, true);

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

	public int getParticleWaves() {
		return dataManager.get(SYNC_PARTICLE_WAVES);
	}

	public void setParticleWaves(int waves) {
		dataManager.set(SYNC_PARTICLE_WAVES, waves);
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

	public boolean getSphere() {
		return dataManager.get(SYNC_IS_SPHERE);
	}

	public void setSphere(boolean sphere) {
		dataManager.set(SYNC_IS_SPHERE, sphere);
	}

	public void setDamageSource(DamageSource source) {
		this.source = source;
	}

	public void setRenderNormal(boolean normal) {
		dataManager.set(SYNC_RENDER_NORMAL, normal);
	}

	public boolean shouldRenderNormal() {
		return dataManager.get(SYNC_RENDER_NORMAL);
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

		this.motionX = this.motionY = this.motionZ = 0;

		if ((this.ticksExisted * getSpeed() * 0.5) > getRange()) {
			this.setDead();
		}
		if (ticksExisted > 140) {
			setDead();
		}

		if (world.isRemote && getOwner() != null) {
			if (shouldRenderNormal() && getParticle() != null) {
				EnumParticleTypes particle = getParticle();
				if (particle != null) {
					if (ticksExisted <= getParticleWaves()) {
						if (!getSphere()) {
							for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (getRange() * getParticleAmount() * ticksExisted)) {
								//Even though the maths is technically wrong, you use sin if you want a shockwave, and cos if you want a sphere (for x).
								double x2 = posX + (ticksExisted * getSpeed()) * Math.sin(angle);
								double y2 = posY;
								double z2 = posZ + (ticksExisted * getSpeed()) * Math.cos(angle);
								Vector speed = new Vector((ticksExisted * getSpeed()) * Math.sin(angle) * (getParticleSpeed() * 10),
										getParticleSpeed() / 2, (ticksExisted * getSpeed()) * Math.cos(angle) * (getParticleSpeed() * 10));
								world.spawnParticle(particle, x2, y2, z2, speed.x(), speed.y(), speed.z());
							}
						}
						if (ticksExisted <= getParticleAmount()) {
							if (getSphere()) {
								double x1, y1, z1, xVel, yVel, zVel;
								for (double theta = 0; theta <= 180; theta += 1) {
									double dphi = (getParticleController() - getParticleAmount()) / Math.sin(Math.toRadians(theta));
									for (double phi = 0; phi < 360; phi += dphi) {
										double rphi = Math.toRadians(phi);
										double rtheta = Math.toRadians(theta);

										x1 = ticksExisted * getSpeed() * Math.cos(rphi) * Math.sin(rtheta);
										y1 = ticksExisted * getSpeed() * Math.sin(rphi) * Math.sin(rtheta);
										z1 = ticksExisted * getSpeed() * Math.cos(rtheta);
										xVel = x1 * getParticleSpeed() * 10;
										yVel = y1 * getParticleSpeed() * 10;
										zVel = z1 * getParticleSpeed() * 10;

										world.spawnParticle(particle, x1 + posX,
												y1 + posY, z1 + posZ, xVel, yVel, zVel);

									}
								}//Creates a sphere. Courtesy of Project Korra's Air Burst!
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean pushLevers(BlockPos pos) {
		if (super.pushLevers(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 4);
		return super.pushLevers(pos);
	}

	@Override
	public boolean pushButtons(BlockPos pos) {
		if (super.pushButtons(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 4);
		return super.pushButtons(pos);

	}

	@Override
	public boolean pushTrapDoors(BlockPos pos) {
		if (super.pushTrapDoors(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 4);
		return super.pushTrapDoors(pos);

	}

	@Override
	public boolean pushDoors(BlockPos pos) {
		if (super.pushGates(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 4);
		return super.pushGates(pos);

	}

	@Override
	public boolean pushGates(BlockPos pos) {
		if (super.pushGates(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 4);
		return super.pushGates(pos);

	}

	@Override
	public double getExpandedHitboxWidth() {
		return (ticksExisted * getSpeed() * 0.5);
	}

	@Override
	public double getExpandedHitboxHeight() {
		return (ticksExisted * getSpeed() * 0.5);
	}

	@Override
	public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
		return getSphere() ? AvatarDamageSource.causeSphericalShockwaveDamage(target, getOwner(), source)
				: AvatarDamageSource.causeShockwaveDamage(target, getOwner(), source);
	}

	@Override
	public Vec3d getKnockback(Entity target) {
		double dist = (getExpandedHitboxWidth() - target.getDistance(this)) > 1 ? (getExpandedHitboxWidth() - target.getDistance(this)) : 1;
		Vec3d velocity = target.getPositionVector().subtract(getPositionVector());
		velocity = velocity.scale(dist).add(0, getKnockbackHeight(), 0);
		double y = velocity.y;
		y = getKnockbackHeight() != 0 ? Math.min(y * getKnockbackMult().y, getKnockbackHeight()) : y;
		return new Vec3d(velocity.x, y, velocity.z);
	}

	@Override
	public Vec3d getKnockbackMult() {
		double amount = getSphere() ? (ticksExisted * getSpeed()) * 2.55 : ticksExisted * 3 * getSpeed();
		return new Vec3d(amount * knockbackMult.x, amount * knockbackMult.y, amount * knockbackMult.z);
	}

	public void setKnockbackMult(Vec3d mult) {
		this.knockbackMult = mult;
	}

	@Override
	public int getPerformanceAmount() {
		return performanceAmount;
	}

	public void setPerformanceAmount(int amount) {
		this.performanceAmount = amount;
	}

	@Override
	public float getXpPerHit() {
		return 4;
	}

	@Override
	public int getFireTime() {
		return fireTime;
	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return super.canCollideWith(entity) || entity instanceof EntityArrow || entity instanceof EntityThrowable;
	}

	@Override
	public SoundEvent[] getSounds() {
		return new SoundEvent[0];
	}

	@Override
	public void spawnExplosionParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {

	}

	@Override
	public boolean shouldDissipate() {
		return false;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public boolean isShockwave() {
		return true;
	}
}

