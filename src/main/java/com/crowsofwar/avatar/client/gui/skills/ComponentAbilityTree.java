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
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSAbilityPath;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentAbilityTree extends UiComponent {
	
	private final BendingAbility ability;
	
	private boolean wasDown;
	
	public ComponentAbilityTree(BendingAbility ability) {
		this.ability = ability;
		
		this.wasDown = false;
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
		
		// Draw levels I, II, III
		for (int i = 0; i < reachedLevel.length; i++) {
			drawTexturedModalRect(i * 31, 0, i * 16, reachedLevel[i] ? 240 : 224, 16, 16);
			
			// Draw bar
			if (i != reachedLevel.length - 1) {
				drawTexturedModalRect(i * 31 + 16, (16 - 8) / 2, 80, 240, 15, 8);
				
				if (reachedLevel[i]) {
					float xp = data.getLevel() == i ? data.getXp() : 100;
					drawTexturedModalRect(i * 31 + 16, (16 - 8) / 2, 80, 248, (int) (xp / 100 * 15), 8);
				}
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
		
		AbilityTreePath path = data.getPath();
		if (path == AbilityTreePath.FIRST) {
			drawTexturedModalRect(3 * 31 - 2, -14, 0, 204, 20, 20);
		} else if (path == AbilityTreePath.SECOND) {
			drawTexturedModalRect(3 * 31 - 2, 10, 0, 204, 20, 20);
		}
		
		boolean down = Mouse.isButtonDown(0);
		if (down && !wasDown) {
			int mouseX = Mouse.getX(), mouseY = screenHeight() - Mouse.getY();
			float minX = coordinates().xInPixels() + scaleFactor() * (48 + 30 + 16);
			float maxX = minX + scaleFactor() * 16;
			float minY1 = coordinates().yInPixels() + scaleFactor() * (-12);
			float maxY1 = minY1 + scaleFactor() * 16;
			float minY2 = coordinates().yInPixels() + scaleFactor() * (12);
			float maxY2 = minY2 + scaleFactor() * 16;
			
			if (mouseX >= minX && mouseX <= maxX) {
				if (mouseY >= minY1 && mouseY <= maxY1 && path != AbilityTreePath.FIRST) {
					System.out.println("Click 1");
					AvatarMod.network.sendToServer(new PacketSAbilityPath(ability, AbilityTreePath.FIRST));
				}
				if (mouseY >= minY2 && mouseY <= maxY2 && path != AbilityTreePath.SECOND) {
					System.out.println("Click 2");
					AvatarMod.network.sendToServer(new PacketSAbilityPath(ability, AbilityTreePath.SECOND));
				}
			}
			
		}
		wasDown = down;
		
	}
	
}
