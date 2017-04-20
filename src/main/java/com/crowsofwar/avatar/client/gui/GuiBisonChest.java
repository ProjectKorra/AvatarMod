package com.crowsofwar.avatar.client.gui;

import static net.minecraft.client.Minecraft.getMinecraft;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.ContainerBisonInventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBisonChest extends GuiContainer {
	
	private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation(
			"textures/gui/container/horse.png");
	
	private final IInventory playerInventory;
	private final IInventory bisonInventory;
	private final EntitySkyBison bison;
	private float mousePosx;
	private float mousePosY;
	
	public GuiBisonChest(IInventory playerInv, IInventory bisonInventory, EntitySkyBison bison) {
		super(new ContainerBisonInventory(playerInv, bisonInventory, bison, getMinecraft().thePlayer));
		this.playerInventory = playerInv;
		this.bisonInventory = bisonInventory;
		this.bison = bison;
		this.allowUserInput = false;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int color = 0x404040;
		this.fontRendererObj.drawString(bisonInventory.getDisplayName().getUnformattedText(), 8, 6, color);
		this.fontRendererObj.drawString(playerInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, color);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		
		if (this.horseEntity instanceof AbstractChestHorse) {
			AbstractChestHorse abstractchesthorse = (AbstractChestHorse) this.horseEntity;
			
			if (abstractchesthorse.func_190695_dh()) {
				this.drawTexturedModalRect(i + 79, j + 17, 0, this.ySize,
						abstractchesthorse.func_190696_dl() * 18, 54);
			}
		}
		
		if (this.horseEntity.func_190685_dA()) {
			this.drawTexturedModalRect(i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
		}
		
		if (this.horseEntity.func_190677_dK()) {
			if (this.horseEntity instanceof EntityLlama) {
				this.drawTexturedModalRect(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
			} else {
				this.drawTexturedModalRect(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
			}
		}
		
		GuiInventory.drawEntityOnScreen(i + 51, j + 60, 17, i + 51 - this.mousePosx,
				j + 75 - 50 - this.mousePosY, this.horseEntity);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mousePosx = mouseX;
		this.mousePosY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}