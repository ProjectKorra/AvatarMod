package com.maxandnoah.avatar.server;

import java.util.ArrayList;
import java.util.List;

import com.maxandnoah.avatar.common.controls.AvatarControl;
import com.maxandnoah.avatar.common.controls.IControlsHandler;

public class AvatarKeybindingServer implements IControlsHandler {

	@Override
	public boolean isControlPressed(AvatarControl control) {
		return false;
	}
	
	@Override
	public int getKeyCode(AvatarControl control) {
		return -1;
	}

	@Override
	public List<AvatarControl> getAllPressed() {
		return new ArrayList<AvatarControl>();
	}

}
