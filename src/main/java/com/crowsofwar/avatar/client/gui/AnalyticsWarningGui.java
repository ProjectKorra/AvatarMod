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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class AnalyticsWarningGui extends GuiScreen {
	
	@Override
	public void initGui() {
		this.buttonList.clear();
		
		this.buttonList
				.add(new GuiButton(0, (width - 200) / 2, height - height / 5, 200, 20, "To Main Menu"));
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiMainMenu());
		} else if (button.id == 1) {
			this.mc.shutdown();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		// @formatter:off
		String[] lines = {
			"Avatar Mod 2 - Analytics Notification",
			"",
			"Hello! We would like to notify you of a new feature of Avatar Mod 2.",
			"We've introduced a statistic feature called " + TextFormatting.BOLD + "analytics"
					+ TextFormatting.RESET + ",",
			"which gathers anonymous statistics on what features of AV2 players are using.",
			"This is completely anonymous and the statistics are only used to improve avatar mod.",
			"(Not used for commercial purposes)",
			"Detailed info is available here: http://bit.ly/2Bda6EY"

		};
		// @formatter:on
		
		int y = height / 6;
		for (String ln : lines) {
			drawString(fontRenderer, ln, (width - fontRenderer.getStringWidth(ln)) / 2, y, 0xffffff);
			y += fontRenderer.FONT_HEIGHT + 2;
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}