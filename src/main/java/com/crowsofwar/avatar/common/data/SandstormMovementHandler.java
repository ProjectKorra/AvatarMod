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
	private Vector targetVelocity;

	public SandstormMovementHandler(EntitySandstorm sandstorm) {
		this.sandstorm = sandstorm;
		this.targetVelocity = null;
	}

	public void update() {
		if (targetVelocity != null) {

			Vector nextVelocity = sandstorm.velocity().plus(targetVelocity.dividedBy(10));

			if (nextVelocity.sqrMagnitude() > targetVelocity.sqrMagnitude()) {
				nextVelocity = nextVelocity.normalize().times(targetVelocity.magnitude());
			}

			sandstorm.setVelocity(nextVelocity);

		}
	}

	public void setTargetVelocity(@Nullable Vector targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

}
