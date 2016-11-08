package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
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
		super(1, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);
		
		requireRaytrace(-1, true);
		
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		BendingController controller = (BendingController) BendingManager
				.getBending(BendingType.EARTHBENDING);
		
		AvatarPlayerData data = context.getData();
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(controller);
		
		EntityFloatingBlock floating = ebs.getPickupBlock();
		if (floating != null) {
			// TODO Verify look at block
			VectorI looking = context.getClientLookBlock();
			EnumFacing lookingSide = context.getLookSide();
			if (looking != null && lookingSide != null) {
				looking.offset(lookingSide);
				
				floating.setBehavior(new FloatingBlockBehavior.Place(looking.toBlockPos()));
				Vector force = looking.precision().minus(new Vector(floating));
				force.normalize();
				floating.velocity().add(force);
				ebs.dropBlock();
				
				controller.post(new FloatingBlockEvent.BlockPlaced(floating, context.getPlayerEntity()));
				
				context.removeStatusControl(THROW_BLOCK);
				
				data.getAbilityData(AbilityPickUpBlock.INSTANCE).addXp(SKILLS_CONFIG.blockPlaced);
				
				return true;
			}
			return false;
		}
		
		return true;
		
	}
	
}
