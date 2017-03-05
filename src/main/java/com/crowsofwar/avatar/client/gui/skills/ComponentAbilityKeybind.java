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

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;

import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentAbilityKeybind extends UiComponent {
	
	private final BendingAbility ability;
	private String currentText;
	private int color;
	
	private boolean editing;
	private Conflictable conflict;
	
	public ComponentAbilityKeybind(BendingAbility ability) {
		this.ability = ability;
		this.currentText = "";
		this.color = 0xffffff;
		
		this.editing = false;
		this.conflict = null;
		
		updateText();
		
	}
	
	@Override
	protected float componentWidth() {
		return mc.fontRendererObj.getStringWidth(currentText);
	}
	
	@Override
	protected float componentHeight() {
		return mc.fontRendererObj.FONT_HEIGHT;
	}
	
	@Override
	protected void componentDraw(float partialTicks) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Update the current text and color based on current keybind, whether
	 * editing, etc.
	 */
	private void updateText() {
		
		// Keycode mapped to this ability - may be null!
		Integer keymapping = CLIENT_CONFIG.keymappings.get(ability);
		
		String key;
		
		if (hasConflict()) {
			color = 0xff0000;
			key = "conflict";
		} else if (editing) {
			color = 0xff5962;
			key = "editing";
		} else {
			color = 0xffffff;
			key = keymapping != null ? "set" : "none";
		}
		
		currentText = I18n.format("avatar.key." + key + "1",
				keymapping == null ? "no key" : GameSettings.getKeyDisplayString(keymapping));
		
	}
	
	private boolean hasConflict() {
		return conflict != null;
	}
	
	interface Conflictable {
		String getName();
	}
	
	private static Conflictable conflictableKeybinding(KeyBinding keybind) {
		return () -> GameSettings.getKeyDisplayString(keybind.getKeyCode());
	}
	
	private static Conflictable conflictableAbility(BendingAbility ability) {
		return () -> GameSettings.getKeyDisplayString(CLIENT_CONFIG.keymappings.get(ability));
	}
	
}
