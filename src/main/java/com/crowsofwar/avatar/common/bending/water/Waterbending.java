package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WATER_ARC;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WAVE;
import static com.crowsofwar.avatar.common.bending.BendingType.WATERBENDING;
import static com.crowsofwar.avatar.common.controls.AvatarControl.KEY_WATERBENDING;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.BendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

import net.minecraft.nbt.NBTTagCompound;

public class Waterbending extends BendingController {
	
	private BendingMenuInfo menu;
	private final BendingAbility abilityWaterArc, abilityWave;
	
	public Waterbending() {
		addAbility(this.abilityWaterArc = ABILITY_WATER_ARC);
		addAbility(this.abilityWave = ABILITY_WAVE);
		
		Color base = new Color(228, 255, 225);
		Color edge = new Color(60, 188, 145);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
				new ThemeColor(icon, base)), KEY_WATERBENDING, abilityWaterArc, abilityWave);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public BendingType getType() {
		return WATERBENDING;
	}
	
	@Override
	public BendingState createState(AvatarPlayerData data) {
		return new WaterbendingState(data);
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "waterbending";
	}
	
}
