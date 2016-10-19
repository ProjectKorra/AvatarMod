package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowFire extends StatusControl {
	
	public StatCtrlThrowFire() {
		super(6, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		EntityPlayer player = context.getPlayerEntity();
		AvatarPlayerData data = context.getData();
		
		FirebendingState bendingState = (FirebendingState) data
				.getBendingState(BendingManager.getBending(BendingType.FIREBENDING));
		
		if (bendingState.isManipulatingFire()) {
			
			EntityFireArc water = bendingState.getFireArc();
			
			Vector force = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
					Math.toRadians(player.rotationPitch));
			force.mul(10);
			water.velocity().add(force);
			water.setGravityEnabled(true);
			
			bendingState.setNoFireArc();
			data.sendBendingState(bendingState);
			
			return true;
			
		}
		
		return false;
	}
	
}
