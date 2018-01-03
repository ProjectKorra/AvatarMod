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
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.List;

import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;

/**
 * @author CrowsOfWar
 */
public class ComponentAbilityTree extends UiComponent {

	private final Ability ability;
	private final ComponentInventorySlots slot1, slot2;

	public ComponentAbilityTree(Ability ability, ComponentInventorySlots slot1,
								ComponentInventorySlots slot2) {
		this.ability = ability;
		this.slot1 = slot1;
		this.slot2 = slot2;
	}

	@Override
	protected float componentWidth() {
		return 64 + 30 + 24 - 1;
	}

	@Override
	protected float componentHeight() {
		return 24 + 18;
	}

	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {

		AbilityData data = AbilityData.get(mc.player, ability.getName());

		mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);

		// @formatter:off
		boolean[] reachedLevel = new boolean[] {
			data.getLevel() >= 0,
			data.getLevel() >= 1,
			data.getLevel() >= 2
		};
		// @formatter:on

		int level123Y = 12;
		int horizontalBarY = 12 + (18 - 8) / 2;
		int diagonalBar1Y = 4;
		int diagonalBar2Y = 20;

		slot1.setVisible(data.getLevel() != 3 && data.getXp() == 100);
		slot2.setVisible(data.getLevel() == 2 && data.getXp() == 100);

		// Draw levels I, II, III
		for (int i = 0; i < reachedLevel.length; i++) {
			drawTexturedModalRect(i * 33, level123Y, i * 18 + 166, reachedLevel[i] ? 220 : 202, 18, 18);

			// Draw horizontal bar
			if (i != reachedLevel.length - 1) {
				drawTexturedModalRect(i * 33 + 18, horizontalBarY, 80, 240, 15, 8);

				if (reachedLevel[i]) {
					float xp = data.getLevel() == i ? data.getXp() : 100;
					drawTexturedModalRect(i * 33 + 18, horizontalBarY, 80, 248, (int) (xp / 100 * 15), 8);
				}
			}

			// Show slot in the next level
			if (i > 0 && !reachedLevel[i] && reachedLevel[i - 1]) {

				slot1.setOffset(fromPixels(i * 33 * scaleFactor(), level123Y * 2).plus(coordinates()));
				slot1.useTexture(AvatarUiTextures.skillsGui, 166 + i * 18, 238, 18, 18);

			}

		}

		// Draw pipes between level III and the two different level IVs
		drawTexturedModalRect(reachedLevel.length * 33 - 16, diagonalBar1Y, 80, 224, 16, 16);
		drawTexturedModalRect(reachedLevel.length * 33 - 16, diagonalBar2Y, 80, 208, 16, 16);

		if (data.getLevel() >= 2) {

			float xp = data.getLevel() == 3 ? 100 : data.getXp();

			drawTexturedModalRect(reachedLevel.length * 33 - 16, diagonalBar1Y, 96, 224,
					(int) (xp / 100 * 16), 16);
			drawTexturedModalRect(reachedLevel.length * 33 - 16, diagonalBar2Y, 96, 208,
					(int) (xp / 100 * 16), 16);

		}

		// Draw level IVs
		int level4FirstX = 3 * 33;
		int level4FirstY = 0;
		int level4SecondX = 3 * 33;
		int level4SecondY = 24;

		boolean firstGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.FIRST;
		boolean secondGray = data.getLevel() < 3 || data.getPath() != AbilityTreePath.SECOND;

		if (!firstGray) {
			drawTexturedModalRect(level4FirstX, level4FirstY, 220, 220, 18, 18);
		} else {
			drawTexturedModalRect(level4FirstX, level4FirstY, 220, 202, 18, 18);
		}
		if (!secondGray) {
			drawTexturedModalRect(level4SecondX, level4SecondY, 238, 220, 18, 18);
		} else {
			drawTexturedModalRect(level4SecondX, level4SecondY, 238, 202, 18, 18);
		}

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

		float l123MinY = coordinates().yInPixels() + 12 * scaleFactor();
		float l123MaxY = l123MinY + 18 * scaleFactor();

		float l1MinX = coordinates().xInPixels();
		float l1MaxX = l1MinX + 18 * scaleFactor();
		float l2MinX = coordinates().xInPixels() + 33 * scaleFactor();
		float l2MaxX = l2MinX + 18 * scaleFactor();
		float l3MinX = coordinates().xInPixels() + 66 * scaleFactor();
		float l3MaxX = l3MinX + 18 * scaleFactor();

		float l4MaxX = coordinates().xInPixels() + width();
		float l4MinX = l4MaxX - 18 * scaleFactor();
		float l41MinY = coordinates().yInPixels();
		float l41MaxY = l41MinY + 18 * scaleFactor();
		float l42MinY = coordinates().yInPixels() + height() - 18 * scaleFactor();
		float l42MaxY = l42MinY + 18 * scaleFactor();

		String level = null;

		if (mouseX >= l1MinX && mouseX <= l1MaxX && mouseY >= l123MinY && mouseY <= l123MaxY) {
			level = "lvl1";
		}
		if (mouseX >= l2MinX && mouseX <= l2MaxX && mouseY >= l123MinY && mouseY <= l123MaxY) {
			level = "lvl2";
		}
		if (mouseX >= l3MinX && mouseX <= l3MaxX && mouseY >= l123MinY && mouseY <= l123MaxY) {
			level = "lvl3";
		}
		if (mouseX >= l4MinX && mouseX <= l4MaxX && mouseY >= l41MinY && mouseY <= l41MaxY) {
			level = "lvl4_1";
		}
		if (mouseX >= l4MinX && mouseX <= l4MaxX && mouseY >= l42MinY && mouseY <= l42MaxY) {
			level = "lvl4_2";
		}

		if (level != null) {
			return Arrays
					.asList(I18n.format("avatar.ability." + ability.getName() + "." + level).split(" ;; "));
		}

		return null;
	}

}
