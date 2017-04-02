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

import java.util.ArrayList;
import java.util.List;

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
		
		if (ability == BendingAbility.ABILITY_AIR_GUST) {
			// System.out.println(Arrays.toString(reachedLevel));
		}
		
		slot1.setVisible(data.getLevel() != 3 && data.getXp() == 100);
		slot2.setVisible(data.getLevel() == 2 && data.getXp() == 100);
		
		// Draw levels I, II, III
		for (int i = 0; i < reachedLevel.length; i++) {
			drawTexturedModalRect(i * 33, 0, i * 18 + 166, reachedLevel[i] ? 220 : 202, 18, 18);
			
			// Draw bar
			if (i != reachedLevel.length - 1) {
				drawTexturedModalRect(i * 33 + 18, (18 - 8) / 2, 80, 240, 15, 8);
				
				if (reachedLevel[i]) {
					float xp = data.getLevel() == i ? data.getXp() : 100;
					drawTexturedModalRect(i * 33 + 18, (18 - 8) / 2, 80, 248, (int) (xp / 100 * 15), 8);
				}
			}
			
			if (i > 0 && !reachedLevel[i] && reachedLevel[i - 1]) {
				slot1.setOffset(Measurement.fromPixels(//
						coordinates().xInPixels() + i * 33 * scaleFactor(), //
						coordinates().yInPixels() + 0));
				slot1.useTexture(AvatarUiTextures.skillsGui, 166 + i * 18, 238, 18, 18);
			}
			
		}
		
		// Draw pipes between level III and the two different level IVs
		drawTexturedModalRect(reachedLevel.length * 33 - 16, -8, 80, 224, 16, 16);
		drawTexturedModalRect(reachedLevel.length * 33 - 16, 8, 80, 208, 16, 16);
		
		if (data.getLevel() >= 2) {
			float xp = data.getLevel() == 3 ? 100 : data.getXp();
			
			drawTexturedModalRect(reachedLevel.length * 33 - 16, -8, 96, 224, (int) (xp / 100 * 16), 16);
			drawTexturedModalRect(reachedLevel.length * 33 - 16, 8, 96, 208, (int) (xp / 100 * 16), 16);
		}
		
		// Draw level IVs
		boolean firstGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.FIRST;
		boolean secondGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.SECOND;
		
		if (!firstGray) {
			drawTexturedModalRect(3 * 33, -12, 220, 220, 18, 18);
		} else {
			drawTexturedModalRect(3 * 33, -12, 220, 202, 18, 18);
		}
		if (!secondGray) {
			drawTexturedModalRect(3 * 33, 12, 238, 220, 18, 18);
		} else {
			drawTexturedModalRect(3 * 33, 12, 238, 202, 18, 18);
		}
		
		int level4FirstX = 3 * 33;
		int level4FirstY = -12;
		int level4SecondX = 3 * 33;
		int level4SecondY = 12;
		
		if (data.getLevel() == 2) {
			
			float s1x = coordinates().xInPixels() + level4FirstX * scaleFactor();
			float s1y = coordinates().yInPixels() + level4FirstY * scaleFactor();
			float s2x = coordinates().xInPixels() + level4SecondX * scaleFactor();
			float s2y = coordinates().yInPixels() + level4SecondY * scaleFactor();
			
			slot1.setOffset(Measurement.fromPixels(s1x, s1y));
			slot1.useTexture(AvatarUiTextures.skillsGui, 220, 238, 18, 18);
			slot1.setVisible(true);
			slot2.useTexture(AvatarUiTextures.skillsGui, 238, 238, 18, 18);
			slot2.setOffset(Measurement.fromPixels(s2x, s2y));
			slot2.setVisible(true);
			
		}
		
	}
	
	@Override
	public List<String> getTooltip(float mouseX, float mouseY) {
		List<String> lines = new ArrayList<>();
		lines.add("Hello!");
		lines.add("Description");
		return lines;
	}
	
}
