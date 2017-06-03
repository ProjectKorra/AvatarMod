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

package com.crowsofwar.avatar.common.controls;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.AvatarMod;

/**
 * Represents all controls needed to access by AvatarMod. This includes:
 * <ul>
 * <li>Keybindings added by AvatarMod
 * <li>Vanilla keybindings
 * <li>Special controls from AvatarMod like mouse button up/down
 * </ul>
 * 
 */
public class AvatarControl {
	
	// @formatter:off
	public static AvatarControl
		KEY_EARTHBENDING,
		KEY_FIREBENDING,
		KEY_WATERBENDING,
		KEY_AIRBENDING,
		KEY_SKILLS,
		KEY_TRANSFER_BISON,
		CONTROL_LEFT_CLICK,
		CONTROL_RIGHT_CLICK,
		CONTROL_MIDDLE_CLICK,
		CONTROL_LEFT_CLICK_DOWN,
		CONTROL_RIGHT_CLICK_DOWN,
		CONTROL_MIDDLE_CLICK_DOWN,
		CONTROL_SPACE,
		CONTROL_SPACE_DOWN,
		CONTROL_LEFT_CLICK_UP,
		CONTROL_RIGHT_CLICK_UP,
		CONTROL_MIDDLE_CLICK_UP,
		CONTROL_SHIFT;
	// @formatter:off
	
	public static void initControls() {
		
	}
	
	private final String name;
	private KeybindingWrapper kb;
	
	private AvatarControl(String name, boolean keybinding) {
		this.name = name;
		if (keybinding) {
			kb = AvatarMod.proxy.createKeybindWrapper(name);
		}
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
		return kb;
	}
	
	/**
	 * Returns whether this control is linked to a keybinding
	 */
	public boolean isKeybinding() {
		return kb != null;
	}
	
	public boolean isPressed() {
		return isKeybinding() ? kb.isPressed() : AvatarMod.proxy.getKeyHandler().isControlPressed(this);
	}
	
}
