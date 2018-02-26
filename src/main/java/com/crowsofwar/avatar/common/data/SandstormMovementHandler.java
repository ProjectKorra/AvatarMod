package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

/**
 * Handles movement for the sandstorm when the sandstorm has been redirected. When the owner
 * requests the sandstorm be at different velocity (via StatCtrlSandstormRedirect), this makes
 * sure velocity transition is handled gradually and not instantly, to make it look more natural.
 */
public class SandstormMovementHandler {

	/**
	 * The base speed for a sandstorm. The actual speed will be BASE_SPEED *
	 * velocityMultiplier
	 */
	private static final double BASE_SPEED = 8;

	private final EntitySandstorm sandstorm;

	/**
	 * Unit vector
	 */
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

			// Move towards the target velocity

			double desiredSpeed = sandstorm.getVelocityMultiplier() * 8;
			Vector desiredVelocity = targetVelocity.times(desiredSpeed);

			// Transition between current velocity and target velocity by performing a weighted
			// average

			double changeFactor = 0.1;
			sandstorm.setVelocity(sandstorm.velocity().times(1 - changeFactor).plus
					(desiredVelocity.times(changeFactor)));

		} else if (sandstorm.ticksExisted % 2 == 0) {

			// Ensure the sandstorm is moving at the correct speed

			// Don't do this when changing directions because keeping a constant speed interferes
			// with process of changing directions - when turning around, speed SHOULD decrease
			// temporarily

			double desiredSpeed = sandstorm.getVelocityMultiplier() * 8;
			Vector newVelocity = sandstorm.velocity().normalize().times(desiredSpeed);
			sandstorm.setVelocity(newVelocity);

		}
	}

	/**
	 * Set the target velocity vector to the given value. Target velocity is a unit vector
	 * pointing in the desired direction which the sandstorm will move towards.
	 */
	public void setTargetVelocity(@Nullable Vector targetVelocity) {
		this.targetVelocity = targetVelocity;
	}

}
