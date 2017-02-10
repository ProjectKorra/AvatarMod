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

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GuiSkillsNew extends GuiContainer implements AvatarGui {
	
	public GuiSkillsNew() {
		super(new ContainerSkillsGui(Minecraft.getMinecraft().thePlayer));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		int iconSize = 256, iconCrop = 80;
		
		enableBlend();
		
		GlStateManager.translate((width - iconSize) / 2f, height / 2f - iconSize + iconCrop, 0);
		
		mc.renderEngine.bindTexture(AvatarUiTextures.getAbilityTexture(BendingAbility.ABILITY_AIR_BUBBLE));
		drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		
		disableBlend();
		
	}
	
}
