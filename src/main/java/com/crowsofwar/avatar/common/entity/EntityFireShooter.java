package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.FireShooterBehaviour;
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

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireShooterBehaviour.Idle());
	}

	@Override
	public float getAoeDamage() {
		return aoeDamage;
	}

	public void setAoeDamage(float damage) {
		this.aoeDamage = damage;
	}

	@Override
	public boolean isPiercing() {
		return true;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public boolean shouldDissipate() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehaviour(getBehaviour());
	}

	public FireShooterBehaviour getBehaviour() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehaviour(FireShooterBehaviour behaviour) {
		dataManager.set(SYNC_BEHAVIOR, behaviour);
	}
}
