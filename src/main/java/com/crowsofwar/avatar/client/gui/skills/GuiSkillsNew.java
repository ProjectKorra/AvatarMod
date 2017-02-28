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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;
import static com.crowsofwar.avatar.common.bending.BendingAbility.*;
import static net.minecraft.client.Minecraft.getMinecraft;

import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.ScreenInfo;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.client.uitools.UiTransform;
import com.crowsofwar.avatar.client.uitools.UiTransformBasic;
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
	
	private AbilityTab[] tabs;
	
	private UiComponent testComponent;
	private Frame frame;
	
	public GuiSkillsNew() {
		super(new ContainerSkillsGui(getMinecraft().thePlayer, screenWidth() / scaleFactor(),
				screenHeight() / scaleFactor()));
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
		ScreenInfo.refreshDimensions();
		
		tabs = new AbilityTab[] { new AbilityTab(ABILITY_AIR_BUBBLE), new AbilityTab(ABILITY_AIR_GUST),
				new AbilityTab(ABILITY_AIR_JUMP), new AbilityTab(ABILITY_AIRBLADE) };
		
		// TRANSITION
		
		// UiTransform initial = new UiTransformBasic(testComponent);
		// initial.setOffset(Measurement.fromPixels(0, 100));
		// UiTransform ending = new UiTransformBasic(testComponent);
		// ending.setPosition(StartingPosition.TOP_RIGHT);
		// ending.setOffset(Measurement.fromPixels(-100, 200));
		
		// testComponent.setTransform(new UiTransformTransition(initial, ending,
		// 2));
		
		// testComponent = new ComponentText("Hello!");
		
		Frame frame2 = new Frame();
		frame2.setPosition(Measurement.fromPercent(10, 10));
		frame2.setDimensions(Measurement.fromPercent(80, 80));
		
		frame = new Frame(frame2);
		frame.setPosition(Measurement.fromPercent(frame2, 10, 10));
		frame.setDimensions(Measurement.fromPercent(frame2, 80, 80));
		
		UiTransform inFrame = new UiTransformBasic(testComponent);
		inFrame.setFrame(frame);
		inFrame.setPosition(StartingPosition.TOP_RIGHT);
		
		testComponent.setTransform(inFrame);
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		ScreenInfo.refreshDimensions();
		
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		
		ContainerSkillsGui container = (ContainerSkillsGui) inventorySlots;
		ItemStack scroll = container.getSlot(0).getStack();
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		
		for (AbilityTab tab : tabs) {
			tab.draw(partialTicks);
		}
		
	}
	
}
