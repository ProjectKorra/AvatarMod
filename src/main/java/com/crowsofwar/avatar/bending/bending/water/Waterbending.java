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

package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import com.crowsofwar.avatar.client.gui.MenuTheme.ThemeColor;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Waterbending extends BendingStyle {

	public static final UUID ID = UUID.fromString("33486f81-29cc-4f7e-84ee-972a73b03b95");

	private BendingMenuInfo menu;

	public Waterbending() {
		registerAbilities();
		Color base = new Color(228, 255, 225);
		Color edge = new Color(60, 188, 145);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(new MenuTheme(background, new ThemeColor(edge, edge),
				new ThemeColor(icon, base), 0x57E8F2), this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

	}

	@Override
	public int getTextColour() {
		return 0x0066CC;
	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

	@Override
	public String getName() {
		return "waterbending";
	}

	@Override
	public UUID getId() {
		return ID;
	}

	@Override
	public SoundEvent getRadialMenuSound() {
		return SoundEvents.ENTITY_GENERIC_SWIM;
	}

	@Override
	public TextFormatting getTextFormattingColour() {
		return TextFormatting.BLUE;
	}
}
