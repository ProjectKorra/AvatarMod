package com.crowsofwar.avatar.common.bending.statctrl;

import static com.crowsofwar.avatar.common.bending.statctrl.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.earth.EarthbendingState;
import com.crowsofwar.avatar.common.bending.earth.FloatingBlockEvent;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.util.EnumFacing;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlPlaceBlock extends StatusControl {
	
	public StatCtrlPlaceBlock() {
		super(ctx -> {
			
			BendingController<EarthbendingState> controller = (BendingController<EarthbendingState>) BendingManager
					.getBending(BendingType.EARTHBENDING);
			
			AvatarPlayerData data = ctx.getData();
			EarthbendingState ebs = (EarthbendingState) data.getBendingState(controller);
			
			EntityFloatingBlock floating = ebs.getPickupBlock();
			if (floating != null) {
				// TODO Verify look at block
				VectorI looking = ctx.getClientLookBlock();
				EnumFacing lookingSide = ctx.getLookSide();
				if (looking != null && lookingSide != null) {
					looking.offset(lookingSide);
					
					floating.setBehavior(new FloatingBlockBehavior.Place(looking.toBlockPos()));
					Vector force = looking.precision().minus(new Vector(floating));
					force.normalize();
					floating.velocity().add(force);
					ebs.dropBlock();
					
					controller.post(new FloatingBlockEvent.BlockPlaced(floating, ctx.getPlayerEntity()));
					
					return true;
				}
			}
			
			return false;
			
		}, 1, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR, new Raytrace.Info(-1, true));
	}
	
}
