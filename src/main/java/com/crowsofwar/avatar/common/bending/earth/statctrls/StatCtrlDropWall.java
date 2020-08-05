package com.crowsofwar.avatar.common.bending.earth.statctrls;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.data.StatusControlController.*;

/**
 * @author CrowsOfWar
 */
public class StatCtrlDropWall extends StatusControl {

	public StatCtrlDropWall() {
		super(2, AvatarControl.CONTROL_MIDDLE_CLICK_DOWN, CrosshairPosition.BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		// Wall has no owner so we go for segments
		EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

		List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class, seg -> seg
				.getOwner() == entity);

		for (EntityWallSegment seg : segments) {
			seg.setBehavior(new WallBehavior.Drop());
		}

		ctx.getData().removeStatusControl(PLACE_WALL);
		ctx.getData().removeStatusControl(PUSH_WALL);
		ctx.getData().removeStatusControl(PULL_WALL);

		return true;
	}

}
