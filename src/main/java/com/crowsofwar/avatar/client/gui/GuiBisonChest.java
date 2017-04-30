package com.crowsofwar.avatar.client.gui;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerBisonChest;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBisonChest extends GuiContainer implements AvatarGui {
	
	private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("avatarmod",
			"textures/gui/bison_inventory.png");
	
	private final IInventory playerInventory;
	private final EntitySkyBison bison;
	private float lastMouseX;
	private float lastMouseY;
	
	public GuiBisonChest(IInventory playerInv, EntitySkyBison bison) {
		super(new ContainerBisonChest(playerInv, bison.getInventory(), bison, getMinecraft().thePlayer));
		this.playerInventory = playerInv;
		this.bison = bison;
		this.allowUserInput = false;
		this.xSize = 248;
		this.ySize = 166;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int color = 0x404040;
		this.fontRendererObj.drawString(bison.getInventory().getDisplayName().getUnformattedText(), 8, 6,
				color);
		this.fontRendererObj.drawString(playerInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, color);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
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