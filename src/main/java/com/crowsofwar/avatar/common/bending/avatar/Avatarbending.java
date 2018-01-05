package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;

import java.util.UUID;

public class Avatarbending extends BendingStyle {
	public static UUID ID = UUID.fromString("53076e40-a677-4c51-84e3-0b4814a829ca");
	@Override
	public BendingMenuInfo getRadialMenu() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public UUID getId() {
		return ID;
	}
}
