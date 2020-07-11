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

package com.crowsofwar.avatar.client.controls;

import com.crowsofwar.avatar.AvatarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents all controls needed to access by AvatarMod. This includes:
 * <ul>
 * <li>Keybindings added by AvatarMod
 * <li>Vanilla keybindings
 * <li>Special controls from AvatarMod like mouse button up/down
 * </ul>
 */
public class AvatarControl {

	public static List<AvatarControl> ALL_CONTROLS;

	// @formatter:off
	public static AvatarControl
		KEY_USE_BENDING,
		KEY_BENDING_CYCLE_LEFT,
		KEY_BENDING_CYCLE_RIGHT,
		KEY_SKILLS,
		KEY_SWITCH,
		KEY_TRANSFER_BISON,
		CONTROL_LEFT_CLICK,
		CONTROL_RIGHT_CLICK,
		CONTROL_MIDDLE_CLICK,
		CONTROL_LEFT_CLICK_DOWN,
		CONTROL_RIGHT_CLICK_DOWN,
		CONTROL_MIDDLE_CLICK_DOWN,
		CONTROL_JUMP,
		CONTROL_LEFT_CLICK_UP,
		CONTROL_RIGHT_CLICK_UP,
		CONTROL_MIDDLE_CLICK_UP,
		CONTROL_SHIFT;
	// @formatter:off
	private final String name;
	private KeybindingWrapper kb;
	private boolean needsKeybinding;
	/**
	 * Creates a new AvatarControl. If the parameter <code>keybinding</code> is true, then initializes to the keybinding with the given name.
	 */
	private AvatarControl(String name, boolean keybinding) {
		this.name = name;
		needsKeybinding = keybinding;
		ALL_CONTROLS.add(this);
	}
	
	public static void initControls() {
		ALL_CONTROLS = new ArrayList<>();
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		KEY_USE_BENDING = new AvatarControl("avatar.Bend", true);
		KEY_BENDING_CYCLE_LEFT = new AvatarControl("avatar.BendingCycleLeft", true);
		KEY_BENDING_CYCLE_RIGHT = new AvatarControl("avatar.BendingCycleRight", true);
		KEY_SKILLS = new AvatarControl("avatar.Skills", true);
		KEY_SWITCH = new AvatarControl("avatar.Switch", true);
		KEY_TRANSFER_BISON = new AvatarControl("avatar.TransferBison", true);
		CONTROL_LEFT_CLICK = new AvatarControl(settings.keyBindAttack.getKeyDescription(), false);
		CONTROL_RIGHT_CLICK = new AvatarControl(settings.keyBindUseItem.getKeyDescription(), false);
		CONTROL_MIDDLE_CLICK = new AvatarControl("MiddleClick", false);
		CONTROL_LEFT_CLICK_DOWN = new AvatarControl("LeftClickDown", false);
		CONTROL_RIGHT_CLICK_DOWN = new AvatarControl("RightClickDown", false);
		CONTROL_MIDDLE_CLICK_DOWN = new AvatarControl("MiddleClickDown", false);
		CONTROL_JUMP = new AvatarControl(settings.keyBindJump.getKeyDescription(), true);
		CONTROL_LEFT_CLICK_UP = new AvatarControl("LeftClickUp", false);
		CONTROL_RIGHT_CLICK_UP = new AvatarControl("RightClickUp", false);
		CONTROL_MIDDLE_CLICK_UP = new AvatarControl("MiddleClickUp", false);
		CONTROL_SHIFT = new AvatarControl(settings.keyBindSneak.getKeyDescription(), false);
	}
	
	/**
	 * Get the name of this control. Can be used in localization.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the keybinding for this control. Returns null for controls that aren't linked to a keybinding.
	 */
	@Nullable
	public KeybindingWrapper getKeybinding() {
		if (needsKeybinding && kb == null) {
			kb = AvatarMod.proxy.createKeybindWrapper(name);
		}
		return kb;
	}
	
	/**
	 * Returns whether this control is linked to a keybinding
	 */
	public boolean isKeybinding() {
		return getKeybinding() != null;
	}

	/**
	 * Whether the control was just pressed. Contrary to {@link #isDown()} - which returns true for
	 * the entire time the key is held down - isPressed only returns true for the first moment the
	 * key has been pressed.
	 */
	public boolean isPressed() {
		return isKeybinding() ? getKeybinding().isPressed() : AvatarMod.proxy.getKeyHandler().isControlPressed(this);
	}

	/**
	 * Whether the control is currently held down. Unlike {@link #isPressed()}, this stays true for
	 * the entire duration of the keypress.
	 */
	public boolean isDown() {
		return isKeybinding() ? getKeybinding().isDown() : AvatarMod.proxy.getKeyHandler().isControlDown(this);
	}
	
}
