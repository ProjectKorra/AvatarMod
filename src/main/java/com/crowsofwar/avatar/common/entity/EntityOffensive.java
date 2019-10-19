package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class EntityOffensive extends AvatarEntity implements IOffensiveEntity {

	//Used for all entities that damage things
	private static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> SYNC_LIFETIME = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.VARINT);
	private static final DataParameter<Float> SYNC_HEIGHT = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_WIDTh = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);

	private AxisAlignedBB expandedHitbox;
	private float xp;
	private int fireTime;
	private int performanceAmount;
	private Vec3d piercingKnockback;
	private Vec3d knockbackMult;


	public EntityOffensive(World world) {
		super(world);
		this.expandedHitbox = getEntityBoundingBox();
		this.performanceAmount = 20;
		this.fireTime = 3;
		this.xp = 3;
		this.piercingKnockback = Vec3d.ZERO;
	}

	public float getHeight() {
		return dataManager.get(SYNC_HEIGHT);
	}

	public float getWidth() {
		return dataManager.get(SYNC_WIDTh);
	}

	public float getAvgSize() {
		if (getHeight() == getWidth()) {
			return getHeight();
		} else return (getHeight() + getWidth()) / 2;
	}

	public void setEntitySize(float height, float width) {
		dataManager.set(SYNC_HEIGHT, height);
		dataManager.set(SYNC_WIDTh, width);
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DAMAGE, 1F);
		dataManager.register(SYNC_LIFETIME, 100);
		dataManager.register(SYNC_WIDTh, 1.0F);
		dataManager.register(SYNC_HEIGHT, 1.0F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
			List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, getExpandedHitbox());
			if (!targets.isEmpty()) {
				for (Entity hit : targets) {
					if (canDamageEntity(hit) && this != hit) {
						onCollideWithEntity(hit);
					}
				}
			}
		}
		if (ticksExisted >= getLifeTime()) {
			Dissipate();
		}
		setSize(getWidth(), getHeight());
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
		if (!isPiercing() && isProjectile() && shouldExplode())
			Explode();
		else if (!isPiercing() && shouldDissipate()) {
			Dissipate();
		} else applyPiercingCollision();
		if (entity instanceof AvatarEntity)
			applyElementalContact((AvatarEntity) entity);

	}

	public void Explode() {
		if (world instanceof WorldServer) {
			if (getOwner() != null) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), posX, posY, posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, getSound(), getSoundCategory(), getVolume(),
						getPitch());
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
						getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							attackEntity(entity, false, getAbility(), getXp(), Vec3d.ZERO);
							//Divide the result of the position difference to make entities fly
							//further the closer they are to the player.
							double dist = (getExplosionHitboxGrowth() - entity.getDistance(entity)) > 1 ? (getExplosionHitboxGrowth() - entity.getDistance(entity)) : 1;
							Vec3d velocity = entity.getPositionVector().subtract(this.getPositionVector());
							velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getExplosionHitboxGrowth() / 50, 0);

							double x = velocity.x;
							double y = velocity.y > 0 ? velocity.z : 0.15F;
							double z = velocity.z;
							x *= getKnockbackMult().x;
							y *= getKnockbackMult().y;
							z *= getKnockbackMult().z;

							attackEntity(entity, true, getAbility(), getXp(), Vec3d.ZERO);

							if (!entity.world.isRemote) {
								if (entity.canBePushed()) {
									entity.motionX += x;
									entity.motionY += y;
									entity.motionZ += z;
								}

								if (collided instanceof AvatarEntity) {
									if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
											&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
										AvatarEntity avent = (AvatarEntity) collided;
										avent.addVelocity(x, y, z);
									}
									entity.isAirBorne = true;
									AvatarUtils.afterVelocityAdded(entity);
								}
							}
						}
					}
				}

			}
		}
	}

	public void applyPiercingCollision() {
		double x = getPiercingKnockback().x * getKnockbackMult().x;
		double y = getPiercingKnockback().y * getKnockbackMult().y;
		y = y > 0 ? y : 0.15F;
		double z = getPiercingKnockback().z * getKnockbackMult().z;
		List<Entity> collided = world.getEntitiesInAABBexcluding(this, getExpandedHitbox(), entity -> entity != getOwner());
		if (!collided.isEmpty()) {
			for (Entity entity : collided) {
				if (entity != getOwner() && entity != null && getOwner() != null) {
					attackEntity(entity, false, getAbility(), getXp(), new Vec3d(x, y, z));
				}
			}

		}
	}

	public void Dissipate() {
		if (world instanceof WorldServer) {
			if (getOwner() != null) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), posX, posY, posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, getSound(), getSoundCategory(), getVolume(),
						getPitch());
			}
		}
		Vec3d vel = getVelocity();
		double x = vel.x * getKnockbackMult().x;
		double y = vel.y * getKnockbackMult().y;
		y = y > 0 ? y : 0.15F;
		double z = vel.z * getKnockbackMult().z;
		List<Entity> collided = world.getEntitiesInAABBexcluding(this, getExpandedHitbox(), entity -> entity != getOwner());
		if (!collided.isEmpty()) {
			for (Entity entity : collided) {
				if (entity != getOwner() && entity != null && getOwner() != null) {
					attackEntity(entity, false, getAbility(), getXp(), new Vec3d(x, y, z));
				}
			}

		}
		setDead();
	}

	@Override
	public float getXpPerHit() {
		return xp;
	}

	public void attackEntity(Entity hit, boolean explosionDamage, Ability ability, float xp, Vec3d velocity) {
		if (getOwner() != null && hit != null) {
			AbilityData data = AbilityData.get(getOwner(), ability.getName());
			if (data != null) {
				boolean ds = hit.attackEntityFrom(getDamageSource(hit), explosionDamage ? getAoeDamage() : getDamage());
				if (!ds && hit instanceof EntityDragon) {
					((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, getDamageSource(hit),
							explosionDamage ? getAoeDamage() : getDamage());
					BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());
					data.addXp(xp);

				} else if (hit instanceof EntityLivingBase && ds) {
					BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());
					data.addXp(xp);
					hit.addVelocity(velocity.x, velocity.y, velocity.z);
					hit.setFire(getFireTime());
				}
			}
		}
	}

	@Override
	public boolean onCollideWithSolid() {
		if (isProjectile() && shouldExplode())
			Explode();
		if (isProjectile() && shouldDissipate())
			Dissipate();
		setDead();
		return true;
	}

	public int getLifeTime() {
		return dataManager.get(SYNC_LIFETIME);
	}

	public void setLifeTime(int lifeTime) {
		dataManager.set(SYNC_LIFETIME, lifeTime);
	}

	public float getAoeDamage() {
		return 1;
	}

	public Vec3d getKnockbackMult() {
		return knockbackMult;
	}

	public void setKnockbackMult(Vec3d mult) {
		this.knockbackMult = mult;
	}

	public EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	public int getNumberofParticles() {
		return 50;
	}

	public double getParticleSpeed() {
		return 0.02;
	}

	public int getPerformanceAmount() {
		return this.performanceAmount;
	}

	public void setPerformanceAmount(int amount) {
		this.performanceAmount = amount;
	}

	public SoundEvent getSound() {
		return SoundEvents.ENTITY_GHAST_SHOOT;
	}

	public float getVolume() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	public float getPitch() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	private DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFireDamage(target, getOwner());
	}

	public double getExpandedHitboxWidth() {
		return 0.25;
	}

	public double getExpandedHitboxHeight() {
		return 0.25;
	}

	public int getFireTime() {
		return this.fireTime;
	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	public boolean isPiercing() {
		return false;
	}

	public boolean shouldDissipate() {
		return false;
	}

	public boolean shouldExplode() {
		return true;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return expandedHitbox;
	}

	public double getExplosionHitboxGrowth() {
		return 1;
	}

	public void applyElementalContact(AvatarEntity entity) {

	}

	public float getXp() {
		return this.xp;
	}

	public void setXp(float xp) {
		this.xp = xp;
	}

	public Vec3d getPiercingKnockback() {
		return this.piercingKnockback;
	}

	public void setPiercingKnockback(Vec3d knockback) {
		this.piercingKnockback = knockback;
	}

	@Override
	public boolean canBePushed() {
		return !isPiercing();
	}

	@Override
	public void setEntityBoundingBox(@Nonnull AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(getExpandedHitboxWidth(), getExpandedHitboxHeight(), getExpandedHitboxWidth());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

}
