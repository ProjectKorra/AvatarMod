package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;

import javax.annotation.Nullable;

/**
 * Handles movement for the sandstorm when the sandstorm has been redirected. When the owner
 * requests the sandstorm be at different velocity (via StatCtrlSandstormRedirect), this makes
 * sure velocity transition is handled gradually and not instantly, to make it look more natural.
 */
public class SandstormMovementHandler {

	private final EntitySandstorm sandstorm;
	@Nullable
	private Vector targetVelocity;

	public SandstormMovementHandler(EntitySandstorm sandstorm) {
		this.sandstorm = sandstorm;
		this.targetVelocity = null;
	}

	/**
	 * If the target velocity is set, then transitions current velocity to the target velocity.
	 * Does nothing if there is no target velocity (i.e. the sandstorm hasn't been redirected)
	 * <p>
	 * Only to be used server side
	 */
	public void update() {
		if (targetVelocity != null) {


			double changeFactor = 0.1;
			sandstorm.setVelocity(sandstorm.velocity().times(1 - changeFactor).plus
					(targetVelocity.times(changeFactor)));

		}
	}

	public void setTargetVelocity(@Nullable Vector targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

}
