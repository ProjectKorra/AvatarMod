package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_PICK_UP_BLOCK;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_RAVINE;
import static com.crowsofwar.avatar.common.bending.BendingType.EARTHBENDING;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

public class Earthbending extends BendingController {
	
	private final BendingMenuInfo menu;
	
	private final BendingAbility abilityPickUpBlock, abilityRavine;
	
	public Earthbending() {
		
		addAbility(this.abilityPickUpBlock = ABILITY_PICK_UP_BLOCK);
		addAbility(this.abilityRavine = ABILITY_RAVINE);
		
		Color light = new Color(225, 225, 225);
		Color brown = new Color(79, 57, 45);
		Color gray = new Color(90, 90, 90);
		Color lightBrown = new Color(255, 235, 224);
		ThemeColor background = new ThemeColor(lightBrown, brown);
		ThemeColor edge = new ThemeColor(brown, brown);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon, 0xD6CABF),
				AvatarControl.KEY_EARTHBENDING, abilityPickUpBlock, abilityRavine);
		
	}
	
	@Override
	public BendingType getType() {
		return EARTHBENDING;
	}
	
	@Override
	public BendingState createState(AvatarPlayerData data) {
		return new EarthbendingState(data);
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "earthbending";
	}
	
}
