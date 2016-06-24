package com.maxandnoah.avatar.common;

/**
 * A list of all of the control names.
 *
 */
public enum AvatarControl {
	
	/** No control is pressed */
	NONE(""),
	CONTROL_BENDING_LIST("BendingList"),
	CONTROL_CHEAT_EARTHBENDING("CheatEarthbending"),
	CONTROL_TOGGLE_BENDING("ToggleBending"),
	CONTROL_THROW_BLOCK("ThrowBlock"),
	CONTROL_RADIAL_MENU("RadialMenu");
	
	private String name;
	
	private AvatarControl(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return ordinal();
	}
	
//	public static final String CONTROL_BENDING_LIST = "BendingList";
//	public static final String CONTROL_CHEAT_EARTHBENDING = "CheatEarthbending";
//	public static final String CONTROL_TOGGLE_BENDING = "ToggleBending";
//	public static final String CONTROL_THROW_BLOCK = "ThrowBlock";
//	public static final String CONTROL_RADIAL_MENU = "RadialMenu";
	
}
