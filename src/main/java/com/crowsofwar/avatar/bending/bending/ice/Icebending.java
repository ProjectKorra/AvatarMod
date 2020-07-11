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
package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import com.crowsofwar.avatar.client.gui.MenuTheme.ThemeColor;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class Icebending extends BendingStyle {

	public static final UUID ID = UUID.fromString("a0e4e7d8-b7c0-4ff7-ba6e-5a279bc52848");

	private final BendingMenuInfo menu;

	public Icebending() {
		super(Waterbending.ID);

		registerAbilities();
		ThemeColor bkgd = new ThemeColor(0xEBF4F5, 0xDBE1E2);
		ThemeColor edge = new ThemeColor(0xC5DDDF, 0xACBFC0);
		ThemeColor icon = new ThemeColor(0xFFEBC2, 0xFBE9C3);

		MenuTheme theme = new MenuTheme(bkgd, edge, icon, 0xFFEBC2);
		menu = new BendingMenuInfo(theme, this);

	}

	@Override
	public int getTextColour() {
		return 0xCCFFFF;
	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

	@Override
	public String getName() {
		return "icebending";
	}

	@Override
	public UUID getId() {
		return ID;
	}

	@Override
	public SoundEvent getRadialMenuSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public TextFormatting getTextFormattingColour() {
		return TextFormatting.DARK_AQUA;
	}
}
