package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * Handles movement for the sandstorm, which causes it to move to the location its owner is looking.
 */
public class SandstormMovementHandler {

	private final EntitySandstorm sandstorm;
	@Nullable
	private Vector nextPos;

	public SandstormMovementHandler(EntitySandstorm sandstorm) {
		this.sandstorm = sandstorm;
		this.nextPos = null;
	}

	public void update() {
		EntityLivingBase owner = sandstorm.getOwner();
		if (owner != null) {

			if (sandstorm.ticksExisted % 20 == 0) {

				Raytrace.Result raytrace = Raytrace.getTargetBlock(owner, 15, false);
				nextPos = raytrace.getPosPrecise();

			} else {

				if (nextPos != null) {
					Vector direction = nextPos.minus(sandstorm.position());
					double speed = MathHelper.clamp(nextPos.sqrDist(direction) / 5, 0, 3);
					sandstorm.setVelocity(direction.times(speed));
				}

			}

		}

	}

}
