package com.maxandnoah.avatar.server;

import com.maxandnoah.avatar.common.AvatarControl;
import com.maxandnoah.avatar.common.IKeybindingManager;

public class AvatarKeybindingServer implements IKeybindingManager {

	@Override
	public boolean isKeyPressed(AvatarControl control) {
		return false;
	}

	@Override
	public int getKeyCode(AvatarControl control) {
		return -1;
	}

}
