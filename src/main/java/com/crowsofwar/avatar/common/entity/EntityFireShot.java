package com.crowsofwar.avatar.common.entity;

import net.minecraft.world.World;

public class EntityFireShot extends AvatarEntity {

	public EntityFireShot(World world) {
		super(world);
	}

	@Override
	public boolean isProjectile() {
		return true;
	}
}
