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

import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.config.ConfigClient;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;

/**
 * @author CrowsOfWar
 */
public class ComponentAbilityKeybind extends UiComponent {

	private final Ability ability;
	private String text1, text2;
	private int color;

	private boolean editing;
	private Conflictable conflict;

	public ComponentAbilityKeybind(Ability ability) {
		this.ability = ability;
		this.text1 = this.text2 = "";
		this.color = 0xffffff;

		this.editing = false;
		this.conflict = null;

		updateText();

	}

	private static Conflictable conflictableKeybinding(KeyBinding keybind) {
		return () -> GameSettings.getKeyDisplayString(keybind.getKeyCode());
	}

	private static Conflictable conflictableAbility(Ability ability) {
		return () -> GameSettings.getKeyDisplayString(CLIENT_CONFIG.keymappings.get(ability));
	}

	@Override
	protected float componentWidth() {
		int w1 = mc.fontRenderer.getStringWidth(text1);
		int w2 = mc.fontRenderer.getStringWidth(text2);
		return Math.max(w1, w2);
	}

	@Override
	protected float componentHeight() {
		return mc.fontRenderer.FONT_HEIGHT * 2;
	}

	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {

		FontRenderer fr = mc.fontRenderer;
		fr.drawString(text1, 0, 0, color);
		fr.drawString(text2, 0, fr.FONT_HEIGHT, color);

	}

	/**
	 * Update the current text and color based on current keybind, whether
	 * editing, etc.
	 */
	private void updateText() {

		// Keycode mapped to this ability - may be null!
		Integer keymapping = currentKey();

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

		String keymappingStr = keymapping == null ? "" : GameSettings.getKeyDisplayString(keymapping);
		String conflictStr = conflict == null ? "no conflict" : conflict.getName();

		text1 = I18n.format("avatar.key." + key + "1", keymappingStr);
		text2 = I18n.format("avatar.key." + key + "2", conflictStr);

	}

	private boolean hasConflict() {
		return conflict != null;
	}

	@Override
	protected void click(int button) {

		if (editing) {

			if (button == 0 || button == 1) {
				// Stop & discard edit
				editing = false;
			} else {
				// For MMB, extra mouse buttons
				// Maps this mouse button to the key
				editing = false;
				storeKey(button - 100);
			}

		} else {
			// Start editing
			editing = true;
		}

		updateText();

	}

	@Override
	public void keyPressed(int keyCode) {

		if (editing) {

			if (keyCode == Keyboard.KEY_ESCAPE) {
				editing = false;
				storeKey(null);
				updateText();
			} else {
				editing = false;
				storeKey(keyCode);
				updateText();
			}

		}

	}

	private Integer currentKey() {
		return CLIENT_CONFIG.keymappings.get(ability);
	}

	private boolean hasKeybinding() {
		return currentKey() != null;
	}

	private void storeKey(Integer key) {
		CLIENT_CONFIG.keymappings.put(ability, key);
		ConfigClient.save();
	}

	public boolean isEditing() {
		return editing;
	}

	interface Conflictable {
		String getName();
	}

}
