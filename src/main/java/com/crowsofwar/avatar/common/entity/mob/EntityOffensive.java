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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class EntityOffensive extends AvatarEntity {

	//Used for all entities that damage things
	private static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);

	private AxisAlignedBB expandedHitbox;

	public EntityOffensive(World world) {
		super(world);
		this.expandedHitbox = getEntityBoundingBox();
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
			List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, expandedHitbox);
			if (!targets.isEmpty()) {
				for (Entity hit : targets) {
					if (canDamageEntity(hit) && this != hit) {
						onCollideWithEntity(hit);
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
		if (!isPiercing())
			Explode();
		else applyPiercingCollision();

	}

	public void Explode() {

	}

	public void applyPiercingCollision() {

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

	protected double getExpandedHitboxWidth() {
		return 0.25;
	}

	protected double getExpandedHitboxHeight() {
		return 0.25;
	}

	protected int getFireTime() {
		return 0;
	}

	protected boolean isPiercing() {
		return false;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return expandedHitbox;
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

}
