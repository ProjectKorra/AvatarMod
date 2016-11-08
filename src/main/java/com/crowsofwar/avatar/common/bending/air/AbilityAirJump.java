package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirJump extends AirAbility {
	
	public static BendingAbility INSTANCE;
	
	/**
	 * @param controller
	 */
	public AbilityAirJump() {
		super("air_jump");
		INSTANCE = this;
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		ctx.addStatusControl(StatusControl.AIR_JUMP);
		
	}
	
	@Override
	public int getIconIndex() {
		return 10;
	}
	
	@Override
	public Info getRaytrace() {
		return new Raytrace.Info();
	}
	
}
