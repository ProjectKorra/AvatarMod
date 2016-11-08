package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterAbility extends BendingAbility {
	
	/**
	 * @param name
	 */
	public WaterAbility(String name) {
		super(BendingType.WATERBENDING, name);
	}
	
}
