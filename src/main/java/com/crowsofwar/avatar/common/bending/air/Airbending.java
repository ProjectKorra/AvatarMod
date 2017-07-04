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

import static com.crowsofwar.avatar.common.bending.Ability.*;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

import net.minecraft.nbt.NBTTagCompound;

public class Airbending extends BendingController {
	
	private BendingMenuInfo menu;
	
	public Airbending() {
		addAbility(ABILITY_AIR_GUST);
		addAbility(ABILITY_AIR_JUMP);
		addAbility(ABILITY_AIRBLADE);
		addAbility(ABILITY_AIR_BUBBLE);
		
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
	public int getId() {
		return BendingManager.ID_AIRBENDING;
	}
	
}
