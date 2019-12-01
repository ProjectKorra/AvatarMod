package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	private float xp;
	private int fireTime;
	private int performanceAmount;
	private int ticks = 0, ticksMoving = 0;


	public EntityOffensive(World world) {
		super(world);
		this.performanceAmount = 20;
		this.fireTime = 3;
		this.xp = 3;
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

	public void setEntitySize(float size) {
		dataManager.set(SYNC_HEIGHT, size);
		dataManager.set(SYNC_WIDTh, size);
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	//This just makes the methods easier to use.
	public void Explode() {
		Explode(world, this, getOwner());
	}

	public void applyPiercingCollision() {
		applyPiercingCollision(this);
	}

	public void Dissipate() {
		Dissipate(this);
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
		List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, getExpandedHitbox());
		if (!targets.isEmpty()) {
			for (Entity hit : targets) {
				if (canCollideWith(hit) && this != hit) {
					if (!world.isRemote) {
						onCollideWithEntity(hit);
					}
				}
			}
		}
		if (noClip) {
			IBlockState state = world.getBlockState(getPosition());
			if (state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid) && state.isFullBlock()) {
				ticks++;
			}
			if (ticks > 1) {
				//Checks whether to dissipate first.
				if (shouldDissipate())
					Dissipate();
				else if (shouldExplode())
					Explode();
			}
		}
		if (shouldDissipate() || shouldExplode())
			ticksMoving++;

		if (ticksMoving >= getLifeTime() && (shouldDissipate() || shouldExplode())) {
			if (shouldDissipate())
				Dissipate();
			else if (shouldExplode())
				Explode();
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
			attackEntity(this, entity, false, getKnockback());
			Dissipate();
		}
		else if (isShockwave())
			attackEntity(this, entity, false, getKnockback(entity));
		else applyPiercingCollision();
		if (entity instanceof AvatarEntity)
			applyElementalContact((AvatarEntity) entity);

	}

	@Override
	public Vec3d getKnockback() {
		double x = Math.min(getKnockbackMult().x * motionX, motionX * 2);
		double y = Math.min(0.5, (motionY + 0.3) * getKnockbackMult().y);
		double z = Math.min(getKnockbackMult().z * motionZ, motionZ * 2);
		return new Vec3d(x, y, z);
	}

	@Override
	public float getXpPerHit() {
		return xp;
	}


	@Override
	public boolean onCollideWithSolid() {
		if (isProjectile() && shouldExplode())
			Explode();
		if (isProjectile() && shouldDissipate())
			Dissipate();
		return true;
	}

	public int getLifeTime() {
		return dataManager.get(SYNC_LIFETIME);
	}

	public void setLifeTime(int lifeTime) {
		dataManager.set(SYNC_LIFETIME, lifeTime);
	}

	@Override
	public float getAoeDamage() {
		return 1;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(1, 2, 1);
	}

	@Override
	public EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	@Override
	public int getNumberofParticles() {
		return 50;
	}

	@Override
	public double getParticleSpeed() {
		return 0.02;
	}

	@Override
	public int getPerformanceAmount() {
		return this.performanceAmount;
	}

	public void setPerformanceAmount(int amount) {
		this.performanceAmount = amount;
	}

	@Override
	public float getVolume() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	@Override
	public float getPitch() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	public DamageSource getDamageSource(Entity target) {
		return getDamageSource(target, getOwner());
	}

	@Override
	public double getExpandedHitboxWidth() {
		return 0.25;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return 0.25;
	}

	@Override
	public int getFireTime() {
		return this.fireTime;
	}

	public void setFireTime(int time) {
		this.fireTime = time;
	}

	@Override
	public boolean isPiercing() {
		return false;
	}

	@Override
	public boolean shouldDissipate() {
		return false;
	}

	@Override
	public boolean shouldExplode() {
		return true;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return getExpandedHitbox(this);
	}

	@Override
	public double getExplosionHitboxGrowth() {
		return 1;
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {

	}

	public void setXp(float xp) {
		this.xp = xp;
	}

	@Override
	public boolean canBePushed() {
		return !isPiercing();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	//No relation to getKnockback.
	@Override
	public Vec3d getExplosionKnockbackMult() {
		return new Vec3d(0.4, 0.4, 0.4);
	}

	//Only used in shockwaves
	@Override
	public Vec3d getKnockback(Entity target) {
		Vec3d knockback = Vector.getEntityPos(target).minus(Vector.getEntityPos(this)).normalize().toMinecraft();
		return new Vec3d(knockback.x * getKnockbackMult().x, knockback.y * getKnockbackMult().y, knockback.z * getKnockbackMult().z);
	}
}
