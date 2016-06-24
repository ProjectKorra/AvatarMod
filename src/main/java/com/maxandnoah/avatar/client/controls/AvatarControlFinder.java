package com.maxandnoah.avatar.client.controls;

import com.maxandnoah.avatar.common.AvatarControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AvatarControlFinder {
	
	public static int getID(AvatarControl ctrl) {
		int add = ctrl instanceof AvatarOtherControl ? 200 : 0;
		return add + ctrl.getId();
	}
	
	public static AvatarControl fromID(int id) {
		if (id >= 200) {
			return AvatarOtherControl.values()[id - 200];
		} else {
			return AvatarKeybinding.values()[id];
		}
	}
	
}
