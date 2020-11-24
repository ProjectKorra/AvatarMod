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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.*;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.client.gui.AvatarGui;
import com.crowsofwar.avatar.client.gui.ContainerGetBending;
import com.crowsofwar.avatar.network.packets.PacketSUnlockBending;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;

/**
 * @author CrowsOfWar
 */
public class GetBendingGui extends GuiContainer implements AvatarGui {

	private final ContainerGetBending container;

	private final UiComponentHandler handler;
	private final Frame slotsFrame, buttonsFrame;

	private final UiComponent title, incompatibleMsg, instructions;
	private final ComponentInventorySlots scrollSlots;
	private final ComponentInventorySlots inventoryComp, hotbarComp;
	private final UiComponent[] bendingButtons;

	public GetBendingGui(EntityPlayer player) {
		super(new ContainerGetBending(player));
		this.container = (ContainerGetBending) inventorySlots;

		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();

		handler = new UiComponentHandler();

		slotsFrame = new Frame();
		slotsFrame.setPosition(Measurement.fromPercent((100 - 30) / 2, 10));
		slotsFrame.setDimensions(Measurement.fromPercent(30, 35));

		buttonsFrame = new Frame();
		buttonsFrame.setDimensions(Measurement.fromPercent(40, 35));

		title = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.getBending.title"));
		title.setFrame(slotsFrame);
		title.setPosition(StartingPosition.TOP_CENTER);
		title.setScale(1.5f);
		handler.add(title);

		scrollSlots = new ComponentInventorySlots(container, 3, 1, 0, 2);
		scrollSlots.setFrame(slotsFrame);
		scrollSlots.setPosition(StartingPosition.MIDDLE_BOTTOM);
		// componentScrollSlots.setOffset(Measurement.fromPixels(slotsFrame, 0,
		// componentTitle.height()));
		scrollSlots.useTexture(AvatarUiTextures.getBending, 0, 0, 70, 34);
		scrollSlots.setPadding(Measurement.fromPixels(7, 9));
		handler.add(scrollSlots);

		hotbarComp = new ComponentInventorySlots(container, 9, 1, container.getHotbarIndex(),
				container.getHotbarIndex() + 8);
		hotbarComp.setPosition(StartingPosition.MIDDLE_BOTTOM);
		hotbarComp.setOffset(Measurement.fromPixels(0, -7 * scaleFactor()));
		handler.add(hotbarComp);

		inventoryComp = new ComponentInventorySlots(container, 9, 3, container.getInvIndex(),
				container.getInvIndex() + 26);
		inventoryComp.setPosition(StartingPosition.MIDDLE_BOTTOM);
		inventoryComp.useTexture(AvatarUiTextures.getBending, 0, 34, 176, 90);
		inventoryComp.setPadding(Measurement.fromPixels(7, 7));
		handler.add(inventoryComp);

		incompatibleMsg = new ComponentText(
				TextFormatting.RED + I18n.format("avatar.getBending.incompatible")) {
			@Override
			protected void componentDraw(float partialTicks, boolean mouseHover) {
				float ticks = container.getIncompatibleMsgTicks();
				if (ticks > -1) {
					GlStateManager.enableBlend();
					float alphaFloat = ticks > 40 ? 1 : ticks / 40f;
					int alpha = (int) (alphaFloat * 255);
					if (alpha > 4) {
						drawString(mc.fontRenderer, getText(), 0, 0, 0xffffff | (alpha << 24));
					}
					container.decrementIncompatibleMsgTicks(partialTicks);
					GlStateManager.disableBlend();
				}
			}
		};
		incompatibleMsg.setFrame(slotsFrame);
		incompatibleMsg.setZLevel(999);
		incompatibleMsg.setPosition(StartingPosition.MIDDLE_BOTTOM);
		incompatibleMsg.setOffset(Measurement.fromPixels(slotsFrame, 0, 20));
		handler.add(incompatibleMsg);

		instructions = new ComponentLongText(I18n.format("avatar.getBending.guide"),
				Measurement.fromPercent(50, 0));
		instructions.setFrame(slotsFrame);
		instructions.setPosition(StartingPosition.TOP_CENTER);
		instructions.setOffset(Measurement.fromPixels(slotsFrame, 0, title.height() + 20));
		handler.add(instructions);

		List<UUID> bendingIds = BendingStyles.allMainIds();
		bendingButtons = new UiComponent[bendingIds.size()];
		for (int i = 0; i < bendingButtons.length; i++) {

			UUID bendingId = bendingIds.get(i);

			int u = (i % 2) * 120;
			int v = 124 + 60 * (i / 2);

			UiComponent comp = new ComponentCustomButton(AvatarUiTextures.getBending, u, v, 60, 60, () -> {
				AvatarMod.network.sendToServer(new PacketSUnlockBending(bendingId));
			});

			comp.setFrame(buttonsFrame);
			comp.setScale(0.5f);
			comp.setOffset(Measurement.fromPixels(buttonsFrame, comp.width() * i, 0));

			bendingButtons[i] = comp;
			handler.add(comp);

		}

	}

	@Override
	public void initGui() {
		super.initGui();
		ScreenInfo.refreshDimensions();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		drawDefaultBackground();

		List<UUID> allowedTypes = container.getEligibleBending();

		int visibleBtns = 0;
		for (int i = 0; i < bendingButtons.length; i++) {
			UiComponent btn = bendingButtons[i];
			UUID btnBendingId = BendingStyles.allMainIds().get(i);

			if (allowedTypes.contains(btnBendingId)) {
				btn.setVisible(true);
				btn.setOffset(Measurement.fromPixels(buttonsFrame, btn.width() * visibleBtns, 0));
				visibleBtns++;
			} else {
				btn.setVisible(false);
			}

		}

		adjustButtonsPosition();

		handler.draw(partialTicks, mouseX, mouseY);

	}

	private void adjustButtonsPosition() {

		// Center buttonsFrame by setting its width to the width of all buttons
		// ... then setting its position by centering it based on new width

		float totalWidth = 0;
		for (UiComponent btn : bendingButtons) {
			if (btn.isVisible()) {
				totalWidth += btn.width();
			}
		}

		float yPx = buttonsFrame.getDimensions().yInPixels();
		buttonsFrame.setDimensions(Measurement.fromPixels(totalWidth, yPx));
		buttonsFrame
				.setPosition(Measurement.fromPercent((100 - buttonsFrame.getDimensions().xInPercent()) / 2,
						slotsFrame.getCoordsMax().yInPercent()));

	}

}
