package com.crowsofwar.avatar.common.statctrl;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowWater extends StatusControl {
	
	/**
	 * @param texture
	 * @param subscribeTo
	 * @param position
	 */
	public StatCtrlThrowWater(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		super(texture, subscribeTo, position);
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		EntityPlayer player = context.getPlayerEntity();
		AvatarPlayerData data = context.getData();
		
		WaterbendingState waterState = (WaterbendingState) data
				.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
		
		if (waterState != null) {
			
		}
		
		return true;
	}
	
}
