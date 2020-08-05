package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

/**
 * A Status Control which allows player to change the target velocity of the sandstorm. Then the
 * SandstormMovementHandler gradually transitions current velocity to the target velocity.
 */
public class StatCtrlSandstormRedirect extends StatusControl {

	public StatCtrlSandstormRedirect() {
		super(17, AvatarControl.CONTROL_RIGHT_CLICK_DOWN, CrosshairPosition.RIGHT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();

		List<EntitySandstorm> sandstorms = world.getEntities(EntitySandstorm.class, candidate -> candidate.getOwner() == entity);

		if (!sandstorms.isEmpty()) {

			Raytrace.Result raytrace = Raytrace.getTargetBlock(entity, 40, true);
			if (raytrace.hitSomething()) {
				Vector hitPos = raytrace.getPosPrecise();

				for (EntitySandstorm sandstorm : sandstorms) {
					Vector currentPos = Vector.getEntityPos(sandstorm);
					Vector newVelocity = hitPos.minus(currentPos).withY(0).normalize();

					sandstorm.getMovementHandler().setTargetVelocity(newVelocity);
					sandstorm.setVelocityMultiplier(sandstorm.getVelocityMultiplier() + 0.5f);
				}

			}

		}

		return true;
	}

}
