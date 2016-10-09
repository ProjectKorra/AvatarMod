package com.crowsofwar.avatar.common.statctrl;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowWater extends StatusControl {
	
	public StatCtrlThrowWater() {
		super(3, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		EntityPlayer player = context.getPlayerEntity();
		AvatarPlayerData data = context.getData();
		
		WaterbendingState bendingState = (WaterbendingState) data
				.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
		
		if (bendingState.isBendingWater()) {
			
			EntityWaterArc water = bendingState.getWaterArc();
			
			Vector force = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
					Math.toRadians(player.rotationPitch));
			force.mul(10);
			water.velocity().add(force);
			water.setGravityEnabled(true);
			
			bendingState.releaseWater();
			data.sendBendingState(bendingState);
			
			return true;
			
		}
		
		return false;
	}
	
}
