package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;

import javax.annotation.Nullable;

/**
 * Handles movement for the sandstorm, which causes it to move to the location its owner is looking.
 */
public class SandstormMovementHandler {

	private final EntitySandstorm sandstorm;
	@Nullable
	private Vector targetPos;

	public SandstormMovementHandler(EntitySandstorm sandstorm) {
		this.sandstorm = sandstorm;
		this.targetPos = null;
	}

	public void update() {
		if (targetPos != null) {

			double targetSpeed = 15;

			Vector targetVelocity = targetPos.minus(sandstorm.position()).times(targetSpeed);
			Vector nextVelocity = sandstorm.velocity().plus(targetVelocity);

			if (nextVelocity.magnitude() > targetSpeed) {
				nextVelocity = nextVelocity.normalize().times(targetSpeed);
			}

			sandstorm.setVelocity(nextVelocity);

		}
	}

	public void setTargetPos(@Nullable Vector targetPos) {
		this.targetPos = targetPos;
	}

}
