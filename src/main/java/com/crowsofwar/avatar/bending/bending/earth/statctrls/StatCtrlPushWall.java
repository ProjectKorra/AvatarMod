package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_WALL;

/**
 * @author Aang23
 */
public class StatCtrlPushWall extends StatusControl {

	public StatCtrlPushWall() {
		super(23, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		// TODO: When upgrade to a5.0 , call setOwner on the wall itself , then lookup
		// based on wall

		// Wall has no owner so we go for segments
		EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

		if (wallSegment.getBehavior().getClass() == WallBehavior.Waiting.class) {

			List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class,
					segment -> segment.getOwner() == entity);

			for (EntityWallSegment segment : segments) {
				if (segment.getBehavior().getClass().equals(WallBehavior.Waiting.class))
					segment.setBehavior(new WallBehavior.Push());
			}

			ctx.getData().removeStatusControl(PLACE_WALL);

			return true;
		} else {
			return false;
		}
	}
}
