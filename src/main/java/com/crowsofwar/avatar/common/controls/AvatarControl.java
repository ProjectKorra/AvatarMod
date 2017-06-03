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

import javax.swing.text.JTextComponent.KeyBinding;

/**
 * A list of all of the control names.
 *
 */
public class AvatarControl {
	
	// @formatter:off
	public static AvatarControl KEY_EARTHBENDING,
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
	
	private String name;
	private boolean isKey;
	
	private AvatarControl(String name) {
		this.name = name;
		this.isKey = name().startsWith("KEY");
	}
	
	private AvatarControl(KeyBinding kb) {
		
	}
	
	/**
	 * Get the name of this control. Can be used in localization.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the Id of this control.
	 * 
	 * @see #findFromId(int)
	 */
	public int getId() {
		return ordinal();
	}
	
	/**
	 * Returns whether this control is a keybinding.
	 */
	public boolean isKeybinding() {
		return isKey;
	}
	
	/**
	 * Find the Avatar control with that Id. If the Id is invalid, throws an
	 * IllegalArgumentException.
	 * 
	 * @param id
	 *            Id of the control, obtained with {@link #getId()}.
	 * @return The control with that Id
	 * @throws IllegalArgumentException
	 *             If that Id refers to no control.
	 */
	public static AvatarControl findFromId(int id) {
		if (id < 0 || id >= values().length)
			throw new IllegalArgumentException("AvatarControl Id '" + id + "' is invalid");
		return values()[id];
	}
	
}
