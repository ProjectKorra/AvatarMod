package com.crowsofwar.avatar.common.controls;

import java.util.List;

/**
 * Manages what controls are pressed. This is designed for the client.
 *
 */
public interface IControlsHandler {
	
	/**
	 * Get whether that control is pressed
	 */
	boolean isControlPressed(AvatarControl control);
	
	/**
	 * Get the key code for that control. It must be a keybinding.
	 */
	int getKeyCode(AvatarControl control);
	
	/**
	 * Get all controls pressed.
	 */
	List<AvatarControl> getAllPressed();
	
}
