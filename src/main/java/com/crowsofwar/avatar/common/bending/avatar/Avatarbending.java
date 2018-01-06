package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;

import java.util.UUID;

public class Avatarbending extends BendingStyle {
	public static UUID ID = UUID.fromString("53076e40-a677-4c51-84e3-0b4814a829ca");

	private final BendingMenuInfo menu;

	public Avatarbending() {

		super(Firebending.ID);

		addAbility("lightning_arc");
		addAbility("lightning_redirect");
		addAbility("lightning_spear");

		MenuTheme.ThemeColor bkgd = new MenuTheme.ThemeColor(0xEBF4F5, 0xDBE1E2);
		MenuTheme.ThemeColor edge = new MenuTheme.ThemeColor(0xC5DDDF, 0xACBFC0);
		MenuTheme.ThemeColor icon = new MenuTheme.ThemeColor(0xFFEBC2, 0xFBE9C3);

		MenuTheme theme = new MenuTheme(bkgd, edge, icon, 0x40E0D0);
		menu = new BendingMenuInfo(theme, this);

	}
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

	@Override
	public String getName() {
		return "avatarbending";
	}

	@Override
	public UUID getId() {
		return ID;
	}
}
