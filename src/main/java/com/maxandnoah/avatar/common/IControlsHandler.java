package com.maxandnoah.avatar.common;

/**
 * Manages what controls are pressed. This is designed for the
 * client.
 *
 */
public interface IControlsHandler {
	
	/**
	 * Get whether that control is pressed
	 */
	boolean isControlPressed(AvatarControl control);
	
	/**
	 * Get the key code for that keybinding
	 */
	int getKeyCode(AvatarKeybinding control);
	
}
