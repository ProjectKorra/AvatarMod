package com.maxandnoah.avatar.server;

import com.maxandnoah.avatar.common.AvatarControl;
import com.maxandnoah.avatar.common.AvatarKeybinding;
import com.maxandnoah.avatar.common.IControlsHandler;

public class AvatarKeybindingServer implements IControlsHandler {

	@Override
	public boolean isControlPressed(AvatarControl control) {
		return false;
	}
	
	@Override
	public int getKeyCode(AvatarKeybinding control) {
		return -1;
	}

}
