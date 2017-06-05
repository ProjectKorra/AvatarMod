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

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<AvatarControl> ALL_CONTROLS;
	
	// @formatter:off
	public static AvatarControl
		KEY_USE_BENDING,
		KEY_BENDING_CYCLE_LEFT,
		KEY_BENDING_CYCLE_RIGHT,
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
		CONTROL_JUMP,
		CONTROL_LEFT_CLICK_UP,
		CONTROL_RIGHT_CLICK_UP,
		CONTROL_MIDDLE_CLICK_UP,
		CONTROL_SHIFT;
	// @formatter:off
	
	public static void initControls() {
		ALL_CONTROLS = new ArrayList<>();
		KEY_USE_BENDING = new AvatarControl("avatar.Bend", true);
		KEY_BENDING_CYCLE_LEFT = new AvatarControl("avatar.BendingCycleLeft", true);
		KEY_BENDING_CYCLE_RIGHT = new AvatarControl("avatar.BendingCycleRight", true);
		KEY_SKILLS = new AvatarControl("avatar.Skills", true);
		KEY_TRANSFER_BISON = new AvatarControl("avatar.TransferBison", true);
		CONTROL_LEFT_CLICK = new AvatarControl("LeftClick", false);
		CONTROL_RIGHT_CLICK = new AvatarControl("RightClick", false);
		CONTROL_MIDDLE_CLICK = new AvatarControl("MiddleClick", false);
		CONTROL_LEFT_CLICK_DOWN = new AvatarControl("LeftClickDown", false);
		CONTROL_RIGHT_CLICK_DOWN = new AvatarControl("RightClickDown", false);
		CONTROL_MIDDLE_CLICK_DOWN = new AvatarControl("MiddleClickDown", false);
		CONTROL_SPACE = new AvatarControl("Space", false);
		CONTROL_SPACE_DOWN = new AvatarControl("SpaceDown", false);
		CONTROL_JUMP = new AvatarControl("key.jump", true);
		CONTROL_LEFT_CLICK_UP = new AvatarControl("LeftClickUp", false);
		CONTROL_RIGHT_CLICK_UP = new AvatarControl("RightClickUp", false);
		CONTROL_MIDDLE_CLICK_UP = new AvatarControl("MiddleClickUp", false);
		CONTROL_SHIFT = new AvatarControl("Shift", false);
	}
	
	private final String name;
	private KeybindingWrapper kb;
	private boolean needsKeybinding;
	
	/**
	 * Creates a new AvatarControl. If the parameter <code>keybinding</code> is true, then initializes to the keybinding with the given name.
	 */
	private AvatarControl(String name, boolean keybinding) {
		this.name = name;
		this.needsKeybinding = keybinding;
		ALL_CONTROLS.add(this);
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
	
	public boolean isPressed() {
		return isKeybinding() ? getKeybinding().isPressed() : AvatarMod.proxy.getKeyHandler().isControlPressed(this);
	}
	
}
