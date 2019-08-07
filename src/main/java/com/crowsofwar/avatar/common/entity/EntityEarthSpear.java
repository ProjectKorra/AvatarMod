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
	protected boolean shouldExplode() {
		return false;
	}

	@Override
	protected boolean shouldDissipate() {
		return false;
	}

	@Override
	protected boolean isPiercing() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed();
	}
}
