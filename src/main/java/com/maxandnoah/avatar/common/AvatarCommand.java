package com.maxandnoah.avatar.common;

/**
 * A type of control which is not activated by a keybinding.
 *
 */
public enum AvatarCommand implements AvatarControl {
	
	/** No control is pressed */
	NONE(""),
	ACTION_TOGGLE_BENDING("ToggleBending"),
	ACTION_THROW_BLOCK("ThrowBlock");
	
	private String name;
	
	private AvatarCommand(String name) {
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
	
}
