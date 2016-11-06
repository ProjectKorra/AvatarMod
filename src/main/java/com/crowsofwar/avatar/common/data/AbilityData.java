package com.crowsofwar.avatar.common.data;

import java.util.function.Consumer;

import com.crowsofwar.avatar.common.bending.BendingAbility;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 * 
 * @author CrowsOfWar
 */
public class AbilityData {
	
	private final BendingAbility ability;
	private final Consumer<Integer> changeListener;
	private int xp;
	
	public AbilityData(BendingAbility ability, Consumer<Integer> onChange) {
		this.ability = ability;
		this.xp = 0;
		this.changeListener = onChange;
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	public int getXp() {
		return xp;
	}
	
	public void setXp(int xp) {
		if (xp < 0) xp = 0;
		if (xp > 100) xp = 100;
		this.xp = xp;
		changeListener.accept(xp);
	}
	
	public void addXp(int xp) {
		setXp(this.xp + xp);
	}
	
}
