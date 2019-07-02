package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.FireShooterBehaviour;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityFireShooter extends AvatarEntity {

	private static final DataParameter<FireShooterBehaviour> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireShooter.class, FireShooterBehaviour.DATA_SERIALIZER);

	/**
	 * @param world The world the entity is spawned in
	 */
	public EntityFireShooter(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}
}
