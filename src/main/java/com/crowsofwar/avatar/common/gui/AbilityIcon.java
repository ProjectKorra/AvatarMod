package com.crowsofwar.avatar.common.gui;

/**
 * Represents an icon for an ability. Icons are 32x32px and found on the
 * ability_icons spritesheet (
 * <code>assets/avatarmod/textures/gui/ability_icons.png</code>).
 * 
 * @author CrowsOfWar
 */
public class AbilityIcon {
	
	private final int u;
	private final int v;
	
	/**
	 * Creates the icon with the given spritesheet index. Indices start at 0 and
	 * go left-to-right. 0 is the first icon, 1 is the second, etc...
	 */
	public AbilityIcon(int index) {
		this.u = (index * 32) % 256;
		this.v = (index / 8) * 32;
	}
	
	public int getMinU() {
		return u;
	}
	
	public int getMaxU() {
		return u + 32;
	}
	
	public int getMinV() {
		return v;
	}
	
	public int getMaxV() {
		return v + 32;
	}
	
}
