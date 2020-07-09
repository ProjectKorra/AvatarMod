/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Airbending extends BendingStyle {

	public static final UUID ID = UUID.fromString("231edc16-639e-4dc4-92f5-924e9102df0f");

	private BendingMenuInfo menu;

	public Airbending() {
		registerAbilities();
		Color light = new Color(220, 220, 220);
		Color dark = new Color(172, 172, 172);
		Color iconClr = new Color(196, 109, 0);
		ThemeColor background = new ThemeColor(light, dark);
		ThemeColor edge = new ThemeColor(dark, dark);
		ThemeColor icon = new ThemeColor(iconClr, iconClr);
		MenuTheme theme = new MenuTheme(background, edge, icon, 0xE8E5DF);
		this.menu = new BendingMenuInfo(theme, this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public int getTextColour() {
		return 0xE5CCFF;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

	@Override
	public String getName() {
		return "airbending";
	}

	@Override
	public UUID getId() {
		return ID;
	}

	@Override
	public SoundEvent getRadialMenuSound() {
		return SoundEvents.BLOCK_FIRE_EXTINGUISH;
	}

	@Override
	public TextFormatting getTextFormattingColour() {
		return TextFormatting.LIGHT_PURPLE;
	}
}
