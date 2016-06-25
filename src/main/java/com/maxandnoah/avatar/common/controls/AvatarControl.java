package com.maxandnoah.avatar.common.controls;

/**
 * Describes any control which can be activated. Such as
 * keybindings, mouse presses, or commands. Only implemented
 * on the client side.
 *
 */
public interface AvatarControl {
	
	String getName();
	
	int getId();
	
	boolean isKeybinding();
	
}
