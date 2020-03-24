package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CrowsOfWar
 * @author Aang23
 */
public class StatCtrlPlaceWall extends StatusControl {

	public StatCtrlPlaceWall() {
		super(21, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.RIGHT_OF_CROSSHAIR);
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
					segment.setBehavior(new WallBehavior.Place());
			}

			ctx.getData().removeStatusControl(StatusControl.DROP_WALL);
			ctx.getData().removeStatusControl(StatusControl.SHOOT_WALL);
			ctx.getData().removeStatusControl(StatusControl.PUSH_WALL);
			ctx.getData().removeStatusControl(StatusControl.PULL_WALL);

			return true;
		} else {
			return false;
		}
	}

}
