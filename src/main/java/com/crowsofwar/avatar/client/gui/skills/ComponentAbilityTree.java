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
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentAbilityTree extends UiComponent {
	
	private final BendingAbility ability;
	private final AbilityData data;
	
	public ComponentAbilityTree(BendingAbility ability) {
		this.ability = ability;
		this.data = AvatarPlayerData.fetcher().fetch(mc.thePlayer).getAbilityData(ability);
	}
	
	@Override
	protected float componentWidth() {
		return 64;
	}
	
	@Override
	protected float componentHeight() {
		return 16;
	}
	
	@Override
	protected void componentDraw(float partialTicks) {
		
		mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
		
		// @formatter:off
		boolean[] reachedLevel = new boolean[] {
			data.getLevel() >= 0,
			data.getLevel() >= 1,
			data.getLevel() >= 2
		};
		// @formatter:on
		
		// Draw levels I, II, III
		for (int i = 0; i < reachedLevel.length; i++) {
			drawTexturedModalRect(i * 31, 0, i * 16, reachedLevel[i] ? 240 : 224, 16, 16);
			drawTexturedModalRect(i * 31 + 16, (16 - 8) / 2, 80, reachedLevel[i] ? 248 : 240, 15, 8);
		}
		
		if (data.getLevel() >= 3) {
			drawTexturedModalRect(3 * 31, 0, data.getPath() == AbilityTreePath.FIRST ? 48 : 64, 240, 16, 16);
		} else {
			drawTexturedModalRect(3 * 31, 0, 47, 224, 16, 16);
		}
		
	}
	
}
