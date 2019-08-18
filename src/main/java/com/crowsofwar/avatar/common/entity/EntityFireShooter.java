package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.FireShooterBehaviour;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityFireShooter extends EntityOffensive {

	//Similar to EntityFlames, except it's bigger, and supports behaviour (works for flamethrower as well)
	private static final DataParameter<FireShooterBehaviour> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireShooter.class, FireShooterBehaviour.DATA_SERIALIZER);

	private float aoeDamage;

	/**
	 * @param world The world the entity is spawned in
	 */
	public EntityFireShooter(World world) {
		super(world);
	}

	public void setAoeDamage(float damage) {
		this.aoeDamage = damage;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireShooterBehaviour.Idle());
	}

	@Override
	protected float getAoeDamage() {
		return aoeDamage;
	}

	@Override
	protected boolean isPiercing() {
		return true;
	}

	@Override
	protected boolean shouldExplode() {
		return false;
	}

	@Override
	protected boolean shouldDissipate() {
		return false;
	}
}
