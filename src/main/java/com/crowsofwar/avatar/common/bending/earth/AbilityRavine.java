package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityRavine extends BendingAbility<EarthbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityRavine(BendingController<EarthbendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return false;
	}
	
	@Override
	public void execute(AvatarPlayerData data) {
		System.out.println("I make a ravine!");
	}
	
	@Override
	public int getIconIndex() {
		return 8;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
