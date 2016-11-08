package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FireAbility extends BendingAbility {
	
	/**
	 * @param name
	 */
	public FireAbility(String name) {
		super(BendingType.FIREBENDING, name);
	}
	
}
