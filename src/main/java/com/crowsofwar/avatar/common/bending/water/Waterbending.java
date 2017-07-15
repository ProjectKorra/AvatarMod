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

package com.crowsofwar.avatar.common.bending.water;

import java.awt.Color;
import java.util.UUID;


import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

import net.minecraft.nbt.NBTTagCompound;

public class Waterbending extends BendingStyle {

	public static UUID ID = UUID.fromString("33486f81-29cc-4f7e-84ee-972a73b03b95");

	private BendingMenuInfo menu;
	
	public Waterbending() {
		addAbility(AbilityWaterArc.ID);
		addAbility(AbilityCreateWave.ID);
		addAbility(AbilityWaterBubble.ID);
		addAbility(AbilityWaterSkate.ID);
		
		Color base = new Color(228, 255, 225);
		Color edge = new Color(60, 188, 145);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
				new ThemeColor(icon, base), 0x57E8F2), this);
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
		return "waterbending";
	}
	
	@Override
	public UUID getId() {
		return ID;
	}
	
}
