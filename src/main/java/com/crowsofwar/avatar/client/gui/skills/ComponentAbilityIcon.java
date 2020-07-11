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
package com.crowsofwar.avatar.client.gui.skills;

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.client.Minecraft;

/**
 * @author CrowsOfWar
 */
public class ComponentAbilityIcon extends ComponentImage {

	public ComponentAbilityIcon(Ability ability) {
		super(AvatarUiTextures.skillsGui, getCurrentLevel(ability) * 16, 240, 16, 16);
	}

	private static int getCurrentLevel(Ability ability) {
		BendingData data = BendingData.get(Minecraft.getMinecraft().player);
		return data.getAbilityData(ability).getLevel();
	}

}
