package com.maxandnoah.avatar.common.controls;

import com.maxandnoah.avatar.client.controls.AvatarControlFinder;

/**
 * Controls other than keybindings.
 *
 */
public enum AvatarOtherControl implements AvatarControl {
	NONE("None"),
	/** Left mouse button is held down */
	CONTROL_LEFT_CLICK("LeftClick"),
	/** Right mouse button is held down */
	CONTROL_RIGHT_CLICK("RightClick"),
	/** Middle mouse button is held down */
	CONTROL_MIDDLE_CLICK("MiddleClick"),
	/** Left mouse button just got pressed */
	CONTROL_LEFT_CLICK_DOWN("LeftClickDown"),
	/** Right mouse button just got pressed */
	CONTROL_RIGHT_CLICK_DOWN("RightClickDown"),
	/** Middle mouse button just got pressed */
	CONTROL_MIDDLE_CLICK_DOWN("MiddleClickDown");

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
