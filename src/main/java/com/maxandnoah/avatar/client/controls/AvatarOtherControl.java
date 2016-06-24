package com.maxandnoah.avatar.client.controls;

import com.maxandnoah.avatar.common.AvatarControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Controls other than keybindings.
 *
 */
@SideOnly(Side.CLIENT)
public enum AvatarOtherControl implements AvatarControl {
	NONE,
	CONTROL_LEFT_CLICK;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}

}
