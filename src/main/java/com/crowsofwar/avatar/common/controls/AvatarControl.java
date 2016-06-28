package com.crowsofwar.avatar.common.controls;

/**
 * A list of all of the control names.
 *
 */
public enum AvatarControl {
	
	/** No control is pressed */
	NONE(""),
	/** Default LeftAlt (will be changed) */
	KEY_EARTHBENDING("RadialMenu"),
	KEY_FIREBENDING("Firebend"),
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
	
	private String name;
	private boolean isKey;
	
	private AvatarControl(String name) {
		this.name = name;
		this.isKey = name().startsWith("KEY");
	}
	
	/**
	 * Get the name of this control. Can be used in localization.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the Id of this control.
	 * @see #findFromId(int)
	 */
	public int getId() {
		return ordinal();
	}

	/**
	 * Returns whether this control is a keybinding.
	 */
	public boolean isKeybinding() {
		return isKey;
	}
	
	/**
	 * Find the Avatar control with that Id. If the Id is invalid,
	 * throws an IllegalArgumentException.
	 * @param id Id of the control, obtained with {@link #getId()}.
	 * @return The control with that Id
	 * @throws IllegalArgumentException If that Id refers to no control.
	 */
	public static AvatarControl findFromId(int id) {
		if (id < 0 || id >= values().length) throw new IllegalArgumentException("AvatarControl Id '" + id + "' is invalid");
		return values()[id];
	}
	
}
