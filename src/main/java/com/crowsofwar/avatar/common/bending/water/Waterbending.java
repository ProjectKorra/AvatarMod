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

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WATER_ARC;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WAVE;
import static com.crowsofwar.avatar.common.bending.BendingType.WATERBENDING;
import static com.crowsofwar.avatar.common.controls.AvatarControl.KEY_WATERBENDING;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
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
		menu = new BendingMenuInfo(
				new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
						new ThemeColor(icon, base), 0x57E8F2),
				KEY_WATERBENDING, abilityWaterArc, abilityWave);
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
