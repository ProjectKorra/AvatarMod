package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFlamethrower extends BendingAbility {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityFlamethrower(BendingController controller) {
		super(controller, "flamethrower");
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		ctx.addStatusControl(StatusControl.START_FLAMETHROW);
	}
	
	@Override
	public int getIconIndex() {
		return 9;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
