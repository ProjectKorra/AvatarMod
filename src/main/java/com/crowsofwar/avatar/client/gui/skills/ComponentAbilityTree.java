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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.Measurement;
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
	private final ComponentInventorySlots slot1, slot2;
	
	public ComponentAbilityTree(BendingAbility ability, ComponentInventorySlots slot1,
			ComponentInventorySlots slot2) {
		this.ability = ability;
		this.slot1 = slot1;
		this.slot2 = slot2;
	}
	
	@Override
	protected float componentWidth() {
		return 64 + 30 + 16;
	}
	
	@Override
	protected float componentHeight() {
		return 16;
	}
	
	@Override
	protected void componentDraw(float partialTicks) {
		
		AbilityData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer).getAbilityData(ability);
		
		mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
		
		// @formatter:off
		boolean[] reachedLevel = new boolean[] {
			data.getLevel() >= 0,
			data.getLevel() >= 1,
			data.getLevel() >= 2
		};
		// @formatter:on
		
		slot1.setVisible(data.getLevel() != 3);
		slot2.setVisible(data.getLevel() == 2);
		
		// Draw levels I, II, III
		for (int i = 0; i < reachedLevel.length; i++) {
			drawTexturedModalRect(i * 31, 0, i * 16, reachedLevel[i] ? 240 : 224, 16, 16);
			
			// Draw bar
			if (i != reachedLevel.length - 1) {
				drawTexturedModalRect(i * 31 + 16, (16 - 8) / 2, 80, 240, 15, 8);
				
				if (reachedLevel[i]) {
					float xp = data.getLevel() == i ? data.getXp() : 100;
					int x = i * 31 + 16;
					int y = (16 - 8) / 2;
					drawTexturedModalRect(x, y, 80, 248, (int) (xp / 100 * 15), 8);
					
				}
			}
			
			if (reachedLevel[i]) {
				slot1.setOffset(Measurement.fromPixels(//
						coordinates().xInPixels() + i * 31 * scaleFactor(), //
						coordinates().yInPixels() + 0));
			}
			
		}
		
		// Draw pipes between level III and the two different level IVs
		drawTexturedModalRect(reachedLevel.length * 31 - 16, -8, 80, 224, 16, 16);
		drawTexturedModalRect(reachedLevel.length * 31 - 16, 8, 80, 208, 16, 16);
		
		if (data.getLevel() >= 2) {
			float xp = data.getLevel() == 3 ? 100 : data.getXp();
			
			drawTexturedModalRect(reachedLevel.length * 31 - 16, -8, 96, 224, (int) (xp / 100 * 16), 16);
			drawTexturedModalRect(reachedLevel.length * 31 - 16, 8, 96, 208, (int) (xp / 100 * 16), 16);
		}
		
		// Draw level IVs
		boolean firstGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.FIRST;
		boolean secondGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.SECOND;
		
		if (!firstGray) {
			drawTexturedModalRect(3 * 31, -12, 48, 240, 16, 16);
		} else {
			drawTexturedModalRect(3 * 31, -12, 48, 224, 16, 16);
		}
		if (!secondGray) {
			drawTexturedModalRect(3 * 31, 12, 64, 240, 16, 16);
		} else {
			drawTexturedModalRect(3 * 31, 12, 48, 224, 16, 16);
		}
		
		int level4FirstX = 3 * 31 - 2;
		int level4FirstY = -14;
		int level4SecondX = 3 * 31 - 2;
		int level4SecondY = 10;
		
		if (data.getLevel() == 2) {
			
			float s1x = coordinates().xInPixels() + level4FirstX * scaleFactor();
			float s1y = coordinates().yInPixels() + level4FirstY * scaleFactor();
			float s2x = coordinates().xInPixels() + level4SecondX * scaleFactor();
			float s2y = coordinates().yInPixels() + level4SecondY * scaleFactor();
			
			slot1.setOffset(Measurement.fromPixels(s1x, s1y));
			slot1.setVisible(true);
			slot2.setOffset(Measurement.fromPixels(s2x, s2y));
			slot2.setVisible(true);
		}
		
	}
	
}
