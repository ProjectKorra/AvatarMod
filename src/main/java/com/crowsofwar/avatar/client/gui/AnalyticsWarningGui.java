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
package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.config.ConfigAnalytics;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AnalyticsWarningGui extends GuiScreen {

	@Override
	public void initGui() {
		buttonList.clear();

		buttonList.add(new GuiButton(0, (width - 200) / 2 - 110, height - height / 3, 200, 20, "Enable analytics (recommended)"));
		buttonList.add(new GuiButton(1, (width - 200) / 2 + 110, height - height / 3, 200, 20, "Disable analytics, don't help out"));

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

		if (button.id == 1) {
			ConfigAnalytics.ANALYTICS_CONFIG.optOutAnalytics();
		}
		ConfigAnalytics.ANALYTICS_CONFIG.dontShowAnalyticsWarning();

		this.mc.displayGuiScreen(new GuiMainMenu());

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		String text = "Avatar Mod 2 - Analytics Notification \n \n " +
				"Hello! We would like to notify you of a new feature of Avatar Mod 2. We've " +
				"introduced a statistic feature called " + TextFormatting.BOLD + "analytics" +
				TextFormatting.RESET + ", which gathers " +
				"anonymous statistics on what features of AV2 players are using. " +
				"This is completely anonymous and the statistics are only used to improve avatar mod. " +
				"(Not used for commercial purposes) \n \n " +
				"Detailed info is available here: http://bit.ly/2Bda6EY";

		// Split into lines, such that each line isn't too long and stretches off the screen
		List<String> lineList = new ArrayList<>();
		String[] words = text.split(" ");
		int currentLineLength = 0;
		StringBuilder currentLine = new StringBuilder();

		for (String word : words) {

			// Want to check if this the same last word instance
			//noinspection StringEquality
			boolean lastWord = word == words[words.length - 1];
			if (lastWord) {
				currentLine.append(word);
			}

			int newLineLength = currentLineLength + fontRenderer.getStringWidth(word);
			if (newLineLength > width * 0.8 || word.equals("\n") || lastWord) {
				lineList.add(currentLine.toString());
				currentLine = new StringBuilder();
				currentLineLength = 0;
			}

			if (!word.equals("\n")) {
				currentLine.append(word).append(' ');
				currentLineLength += fontRenderer.getStringWidth(word);
			}

		}

		int y = height / 6;
		for (String ln : lineList) {
			drawString(fontRenderer, ln, (width - fontRenderer.getStringWidth(ln)) / 2, y, 0xffffff);
			y += fontRenderer.FONT_HEIGHT + 2;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}