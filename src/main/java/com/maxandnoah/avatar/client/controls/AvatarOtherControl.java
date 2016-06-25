package com.maxandnoah.avatar.client.controls;

import com.maxandnoah.avatar.common.AvatarControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Controls other than keybindings.
 *
 */
@SideOnly(Side.CLIENT)
public enum AvatarOtherControl implements AvatarControl {
	NONE("None"),
	CONTROL_LEFT_CLICK("LeftClick"),
	CONTROL_RIGHT_CLICK("RightClick"),
	CONTROL_MIDDLE_CLICK("MiddleClick");

	private final String name;
	
	private AvatarOtherControl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return AvatarControlFinder.getID(this);
	}
	
	@Override
	public boolean isKeybinding() {
		return false;
	}
	
	public static AvatarOtherControl findFromId(int id) {
		return (AvatarOtherControl) AvatarControlFinder.fromID(id);
	}

}
