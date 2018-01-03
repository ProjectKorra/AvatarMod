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

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerBisonChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen;

@SideOnly(Side.CLIENT)
public class GuiBisonChest extends GuiContainer implements AvatarGui {

	private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("avatarmod",
			"textures/gui/bison_inventory.png");

	private final IInventory playerInventory;
	private final EntitySkyBison bison;
	private float lastMouseX;
	private float lastMouseY;

	public GuiBisonChest(IInventory playerInv, EntitySkyBison bison) {
		super(new ContainerBisonChest(playerInv, bison.getInventory(), bison, getMinecraft().player));
		this.playerInventory = playerInv;
		this.bison = bison;
		this.allowUserInput = false;
		this.xSize = 248;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int color = 0x404040;
		fontRenderer.drawString(bison.getInventory().getDisplayName().getUnformattedText(), 8, 6,
				color);
		fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, color);

		if (bison.getInventory().getSizeInventory() == 2) {
			for (int i = 1; i <= 3; i++) {
				String key = "avatar.bisonChestSlots" + i;
				String msg = I18n.format(key);
				fontRenderer.drawString(msg, 80, fontRenderer.FONT_HEIGHT * (i + 1), 0xffffff);
			}
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		drawDefaultBackground();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		drawEntityOnScreen(x + 51, y + 60, 17, x + 51 - lastMouseX, y + 75 - 50 - lastMouseY, bison);

		mc.getTextureManager().bindTexture(INVENTORY_TEXTURE);

		// Draw background of inventory
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		// Draw bison slots, depending on inventory size
		int rows = (int) Math.ceil(bison.getChestSlots() / 9.0);
		drawTexturedModalRect(x + 79, y + 17, 0, 166, 162, rows * 18);

	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
