package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.SandstormMovementHandler;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.world.World;

public class EntitySandstorm extends AvatarEntity {

	private final SandstormMovementHandler movementHandler;

	public EntitySandstorm(World world) {
		super(world);
		setSize(1.2f, 2.2f);
		movementHandler = new SandstormMovementHandler(this);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
//			movementHandler.update();
		}
	}

}
