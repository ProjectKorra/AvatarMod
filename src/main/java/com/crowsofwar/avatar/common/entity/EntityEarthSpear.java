package com.crowsofwar.avatar.common.entity;

import net.minecraft.world.World;

public class EntityEarthSpear extends EntityOffensive {


	public EntityEarthSpear(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
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
	public boolean isPiercing() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed();
	}
}
