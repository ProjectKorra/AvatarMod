package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.gorecore.util.Vector;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowBubble extends StatusControl {
	
	/**
	 */
	public StatCtrlThrowBubble() {
		super(0, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext ctx) {
		AvatarPlayerData data = ctx.getData();
		WaterbendingState state = (WaterbendingState) data
				.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
		
		EntityWaterBubble bubble = state.getBubble();
		if (bubble != null) {
			bubble.setBehavior(new WaterBubbleBehavior.Thrown(bubble));
			bubble.velocity().set(Vector.fromEntityLook(ctx.getPlayerEntity()).mul(10));
		}
		
		return true;
	}
	
}
