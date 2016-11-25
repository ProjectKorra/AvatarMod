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

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_AIR_GUST;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_AIR_JUMP;
import static com.crowsofwar.avatar.common.bending.BendingType.AIRBENDING;

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

import net.minecraft.nbt.NBTTagCompound;

public class Airbending extends BendingController {
	
	private BendingMenuInfo menu;
	private final BendingAbility abilityAirGust, abilityAirJump;
	
	public Airbending() {
		addAbility(this.abilityAirGust = ABILITY_AIR_GUST);
		addAbility(this.abilityAirJump = ABILITY_AIR_JUMP);
		
		Color light = new Color(220, 220, 220);
		Color dark = new Color(172, 172, 172);
		Color iconClr = new Color(196, 109, 0);
		ThemeColor background = new ThemeColor(light, dark);
		ThemeColor edge = new ThemeColor(dark, dark);
		ThemeColor icon = new ThemeColor(iconClr, iconClr);
		MenuTheme theme = new MenuTheme(background, edge, icon, 0xE8E5DF);
		this.menu = new BendingMenuInfo(theme, AvatarControl.KEY_AIRBENDING, abilityAirGust, abilityAirJump);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public BendingType getType() {
		return AIRBENDING;
	}
	
	@Override
	public BendingState createState(AvatarPlayerData data) {
		return new AirbendingState(data);
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "airbending";
	}
	
}
