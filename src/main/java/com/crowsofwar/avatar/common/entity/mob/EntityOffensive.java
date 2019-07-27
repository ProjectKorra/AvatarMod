package com.crowsofwar.avatar.common.entity.mob;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityOffensive extends AvatarEntity {

	//Used for all entities that damage things
	private static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);

	public EntityOffensive(World world) {
		super(world);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
	}

	public void Explode() {

	}

	protected float getAoeDamage() {
		return 1;
	}

	protected Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	protected EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	protected double getParticleSpeed() {
		return 0.02;
	}

	protected double getPerformanceAmount() {
		return 10;
	}

	protected DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFireDamage(target, getOwner());
	}

	protected int getFireTime() {
		return 0;
	}

	protected boolean isPiercing() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return !isPiercing();
	}
}
