package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.bending.BendingType.EARTHBENDING;
import static com.crowsofwar.avatar.common.config.AvatarConfig.CONFIG;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

public class Earthbending extends BendingController {
	
	private final BendingMenuInfo menu;
	
	private final BendingAbility abilityPickUpBlock, abilityThrowBlock, abilityPutBlock, abilityRavine;
	
	public Earthbending() {
		
		addAbility(this.abilityPickUpBlock = new AbilityPickUpBlock(this,
				state -> CONFIG.bendableBlocks.contains(state.getBlock())));
		addAbility(this.abilityThrowBlock = new AbilityThrowBlock(this));
		addAbility(this.abilityPutBlock = new AbilityPutBlock(this));
		addAbility(this.abilityRavine = new AbilityRavine(this));
		
		Color light = new Color(225, 225, 225);
		Color brown = new Color(79, 57, 45);
		Color gray = new Color(90, 90, 90);
		Color lightBrown = new Color(255, 235, 224);
		ThemeColor background = new ThemeColor(lightBrown, brown);
		ThemeColor edge = new ThemeColor(brown, brown);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon), AvatarControl.KEY_EARTHBENDING,
				abilityPickUpBlock, abilityRavine);
		
	}
	
	@Override
	public BendingType getType() {
		return EARTHBENDING;
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
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
