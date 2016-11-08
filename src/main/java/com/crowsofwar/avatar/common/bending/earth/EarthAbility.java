package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class EarthAbility extends BendingAbility {
	
	/**
	 * @param name
	 */
	public EarthAbility(String name) {
		super(BendingManager.getBending(BendingType.EARTHBENDING), name);
	}
	
}
