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

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.stackCompound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GuiButtonScrolls extends GuiButton {
	
	private final Slot scrollSlot;
	
	public GuiButtonScrolls(Container container, int buttonId, int x, int y) {
		super(buttonId, x, y, 18, 18, "");
		this.scrollSlot = container.inventorySlots.get(0);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		
		this.enabled = checkEnabled();
		
		if (this.visible) {
			
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(AvatarUiTextures.skillsGui);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
					&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			
			int offset = this.getHoverState(this.hovered);
			
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			
			//@formatter:off
			
			drawTexturedModalRect(xPosition, yPosition,
					58 + offset * 18, 0,
					width, height);
			
			//@formatter:on
			
			mouseDragged(mc, mouseX, mouseY);
			
			int j = 14737632;
			
			if (packedFGColour != 0) {
				j = packedFGColour;
			} else if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}
			
		}
		
	}
	
	private boolean checkEnabled() {
		return scrollSlot.getHasStack() && stackCompound(scrollSlot.getStack()).getInteger("Points") > 0;
	}
	
}
