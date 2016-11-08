package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AirAbility extends BendingAbility {
	
	/**
	 * @param name
	 */
	public AirAbility(String name) {
		super(BendingManager.getBending(BendingType.AIRBENDING), name);
	}
	
}
