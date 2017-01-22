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

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static net.minecraft.client.renderer.GlStateManager.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.config.ConfigClient;
import com.google.common.collect.EvictingQueue;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiScreen {
	
	private final List<AbilityCard> cards;
	private ScaledResolution res;
	
	private int scroll;
	private int startScroll, startX, lastX;
	private Queue<Integer> recentVelocity = EvictingQueue.create(10);
	
	private float maxX;
	
	private boolean wasMouseDown;
	
	private final BendingController controller;
	private AbilityCard editing;
	
	public SkillsGui(BendingController controller) {
		this.controller = controller;
		this.cards = new ArrayList<>();
		for (BendingAbility ability : controller.getAllAbilities()) {
			cards.add(new AbilityCard(ability));
		}
		lastX = Mouse.getX();
		this.editing = null;
	}
	
	@Override
	public void initGui() {
		this.res = new ScaledResolution(mc);
	}
	
	//@formatter:off
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		{
		pushMatrix();
			float scl = 2.5f;
			String str = TextFormatting.BOLD + I18n.format("avatar." + controller.getControllerName());
			translate((width - fontRendererObj.getStringWidth(str) * scl) / 2, 5, 0);
			scale(scl, scl, 1);
			translate(0, 0, 1);
			drawString(fontRendererObj, str, 0, 0, controller.getRadialMenu().getTheme().getText());
			GlStateManager.color(1, 1, 1, 1);
			
		popMatrix();
		}
		
		// Draw Gui Background
		pushMatrix();
			// Don't need to negate GUI scale...
			// Already at 1600x900
//			GlStateManager.translate(-300, -300, 0);
			
			float zoom = 1.2f;
			
			float scaleX = width / 1600f, scaleY = height / 900f;
			float scale = scaleX > scaleY ? scaleX : scaleY;
			translate(scroll / 30f, 0, 0);
//			scale(2, 2, 1);
			
			float imgWidth = scale * 1600f * zoom, imgHeight = scale * 900f * zoom;
			translate((width - imgWidth) / 2, (height - imgHeight) / 2, 0);
			scale(zoom, zoom, 1);
			scale(scale, scale, 1);
			
//			GlStateManager.scale(scaledWidth / 1600, scaledHeight / 900, 1);
			
			ResourceLocation background = AvatarUiTextures.bgAir;
			BendingType type = controller.getType();
			if (type == BendingType.EARTHBENDING) background = AvatarUiTextures.bgEarth;
			if (type == BendingType.FIREBENDING) background = AvatarUiTextures.bgFire;
			if (type == BendingType.WATERBENDING) background = AvatarUiTextures.bgWater;
			
			mc.renderEngine.bindTexture(background);
			drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 1600, 900, 1600, 900);
		popMatrix();
		
		for (int i = 0; i < cards.size(); i++) {
			maxX = cards.get(i).render(res, i, scroll) - (float) scroll / res.getScaleFactor();
		}
		// maxX is now the last card's maxX
		
	}
	//@formatter:on
	
	@Override
	public void updateScreen() {
		int currentVelocity = 0;
		if (Mouse.isButtonDown(0)) {
			if (!wasMouseDown) {
				wasMouseDown = true;
				startScroll = scroll;
				startX = getMouseX();
			}
			
			currentVelocity = Mouse.getX() - lastX;
			
		} else {
			wasMouseDown = false;
		}
		
		float avg = 0;
		
		int i = 0;
		Iterator<Integer> it = recentVelocity.iterator();
		while (it.hasNext()) {
			int velocity = it.next();
			double mult = (Math.pow(1.2, i)) / (Math.pow(1.2, recentVelocity.size()) - 1);
			avg += velocity * 1.2 * mult;
			i++;
		}
		
		scroll += (avg + currentVelocity) / 2;
		// Positive: scroll left, Negative: scroll right
		if (scroll > 50) {
			scroll = 50;
			currentVelocity = 0;
		}
		if (scroll < -maxX - 50) {
			scroll = (int) (-maxX - 50);
			currentVelocity = 0;
		}
		
		lastX = Mouse.getX();
		recentVelocity.add(currentVelocity);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		// scroll += Mouse.getDWheel() / 3;
		recentVelocity.add(Mouse.getDWheel() / 1);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		if (keyCode == 1 && editing != null) {
			CLIENT_CONFIG.keymappings.remove(editing.getAbility());
			updateConflicts(editing);
			stopEditing();
			ConfigClient.save();
			return;
		}
		
		if (keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
			keyCode = 1;
		}
		if (editing != null) {
			CLIENT_CONFIG.keymappings.put(editing.getAbility(), keyCode);
			updateConflicts(editing);
			stopEditing();
			ConfigClient.save();
		}
		
		super.keyTyped(typedChar, keyCode);
		
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		for (int i = 0; i < cards.size(); i++) {
			AbilityCard card = cards.get(i);
			float _actualWidth = res.getScaledWidth() / 7f;
			float _spacing = res.getScaledWidth() / 8.5f;
			float minX = (int) (i * (_actualWidth + _spacing)) + (float) scroll / res.getScaleFactor();
			float maxX = minX + _actualWidth;
			
			if (x >= minX && x <= maxX) {
				
				float _scaledWidth = 100;
				float _scale = _actualWidth / _scaledWidth;
				float _minY = (res.getScaledHeight() - height) / 2 + 50;
				float minY = _minY + 180 * _scale;
				float maxY = minY + 30 * _scale;
				
				if (y >= minY && y <= maxY) {
					startEditing(card);
				}
				
			}
			
		}
		
		if (button == 1) {
			stopEditing();
		} else if (button != 0 && editing != null) {
			CLIENT_CONFIG.keymappings.put(editing.getAbility(), button - 100);
			updateConflicts(editing);
			stopEditing();
		}
		
	}
	
	private int getMouseX() {
		return Mouse.getX();
	}
	
	public int getMouseScroll() {
		return scroll + getMouseX();
	}
	
	private void startEditing(AbilityCard card) {
		if (editing != null) stopEditing();
		card.setEditing(true);
		this.editing = card;
	}
	
	private void stopEditing() {
		if (editing != null) {
			editing.setEditing(false);
			editing = null;
		}
	}
	
	/**
	 * Ensures that the card's conflict field is correctly set to the
	 * conflicting keybinding; null if none
	 */
	private void updateConflicts(AbilityCard card) {
		card.setConflict(null);
		for (KeyBinding kb : mc.gameSettings.keyBindings) {
			if (CLIENT_CONFIG.keymappings.get(card.getAbility()) != null
					&& (CLIENT_CONFIG.keymappings.get(card.getAbility()) == kb.getKeyCode())) {
				card.setConflict(kb);
				break;
			}
		}
	}
	
}
