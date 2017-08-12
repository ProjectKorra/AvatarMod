package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

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

		EntityWall wall = AvatarEntity.lookupOwnedEntity(world, EntityWall.class, entity);
		if (wall != null) {

			for (int i = 0; i < 5; i++) {
				wall.getSegment(i).setBehavior(new WallBehavior.Drop());
			}

		}

		return true;
	}

}
