package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * A Status Control which allows player to change the target velocity of the sandstorm. Then the
 * SandstormMovementHandler gradually transitions current velocity to the target velocity.
 */
public class StatCtrlSandstormRedirect extends StatusControl {

	public StatCtrlSandstormRedirect() {
		super(0, AvatarControl.CONTROL_RIGHT_CLICK_DOWN, CrosshairPosition.RIGHT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();

		EntitySandstorm sandstorm = AvatarEntity.lookupOwnedEntity(world, EntitySandstorm
				.class, entity);

		if (sandstorm != null) {

			Vector newVelocity = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			newVelocity = newVelocity.times(15);
			sandstorm.getMovementHandler().setTargetVelocity(newVelocity);

		}

		return true;
	}

}
