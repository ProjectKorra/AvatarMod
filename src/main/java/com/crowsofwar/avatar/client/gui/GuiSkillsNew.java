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

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.minecraft.client.renderer.GlStateManager.*;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GuiSkillsNew extends GuiContainer implements AvatarGui {
	
	private GuiButton btnConfirmScroll;
	
	public GuiSkillsNew() {
		super(new ContainerSkillsGui(getMinecraft().thePlayer, screenWidth(), screenHeight()));
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		btnConfirmScroll = addButton(new GuiButton(0, 0, 0, 115, 20, "Remove Roadblock"));
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		
		ContainerSkillsGui container = (ContainerSkillsGui) inventorySlots;
		ItemStack scroll = container.getSlot(0).getStack();
		btnConfirmScroll.enabled = scroll != ItemStack.field_190927_a;
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		
		//@formatter:off
		
		int iconSize = 256, iconCrop = 80;
		int barSize = 80;
		int middlePadding = 20;
		
		btnConfirmScroll.xPosition = inventorySlots.getSlot(0).xDisplayPosition + 20;
		btnConfirmScroll.yPosition = inventorySlots.getSlot(0).yDisplayPosition - 2;
		
		enableBlend();
		
		pushMatrix();
		
			translate((width - iconSize) / 2f, height / 2f - iconSize + iconCrop, 0);
			
			mc.renderEngine.bindTexture(AvatarUiTextures.getAbilityTexture(BendingAbility.ABILITY_AIR_BUBBLE));
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		
		popMatrix();
		
		pushMatrix();
			
			AbilityData abilityData = data.getAbilityData(BendingAbility.ABILITY_AIR_BUBBLE);
			
			translate(width / 2f - barSize - middlePadding / 2f, height / 2f + middlePadding / 2f, 0);
			
			scale(barSize / 40f, barSize / 40f, 1);
			mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
			drawTexturedModalRect(0, 0, 0, 1, 40, 13);
			drawTexturedModalRect(0, 0, 0, 14, (int) (abilityData.getXp() / 100 * 40), 13);
			
		popMatrix();
		
		pushMatrix();
			
			translate((width - 18) / 2 + 19, (height - 18) / 2 + 21, 0);
			
			mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
			drawTexturedModalRect(0, 0, 40, 0, 18, 18);
			
		popMatrix();
		
		pushMatrix();
			
			translate(width - 169, height - 83, 0);
			
			mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
			drawTexturedModalRect(0, 0, 0, 54, 169, 85);
			
		popMatrix();
		
		disableBlend();
		
		//@formatter:on
		
	}
	
	private static int screenWidth() {
		return new ScaledResolution(getMinecraft()).getScaledWidth();
	}
	
	private static int screenHeight() {
		return new ScaledResolution(getMinecraft()).getScaledHeight();
	}
	
}
