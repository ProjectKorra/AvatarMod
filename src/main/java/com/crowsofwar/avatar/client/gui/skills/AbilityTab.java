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
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.resources.I18n;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityTab {
	
	private final BendingAbility ability;
	private UiComponent icon, text;
	
	public AbilityTab(BendingAbility ability) {
		
		this.ability = ability;
		
		this.icon = new ComponentImage(AvatarUiTextures.getAbilityTexture(ability), 0, 0, 256, 256);
		
		this.text = new ComponentText(I18n.format("avatar.ability." + ability.getName()));
		
	}
	
	public void draw(float partialTicks) {
		icon.draw(partialTicks);
		text.draw(partialTicks);
	}
	
}
