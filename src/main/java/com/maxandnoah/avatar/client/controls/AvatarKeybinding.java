package com.maxandnoah.avatar.client.controls;

import com.maxandnoah.avatar.common.AvatarControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A list of all of the control names.
 *
 */
@SideOnly(Side.CLIENT)
public enum AvatarKeybinding implements AvatarControl {
	
	/** No control is pressed */
	NONE(""),
	KEY_BENDING_LIST("BendingList"),
	KEY_CHEAT_EARTHBENDING("CheatEarthbending"),
	KEY_RADIAL_MENU("RadialMenu");
	
	private String name;
	
	private AvatarKeybinding(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public boolean isKeybinding() {
		return true;
	}
	
}
