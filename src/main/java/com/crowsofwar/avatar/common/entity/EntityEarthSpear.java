package com.crowsofwar.avatar.common.entity;

import net.minecraft.world.World;

public class EntityEarthSpear extends EntityOffensive {

	private int performanceAmount;

	public EntityEarthSpear(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	protected int getPerformanceAmount() {
		return performanceAmount;
	}

	@Override
	public void applyPiercingCollision() {
		super.applyPiercingCollision();
	}

	@Override
	public void Explode() {
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
