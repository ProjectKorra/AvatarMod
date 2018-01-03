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

import java.util.List;

/**
 * Manages what controls are pressed. This is designed for the client.
 */
public interface IControlsHandler {

	/**
	 * <strong>For internal use only. To check if a control is pressed, you
	 * should use {@link AvatarControl#isPressed()}.</strong>
	 * <p>
	 * Get whether that control is pressed, only used for non-keybinding
	 * controls
	 */
	boolean isControlPressed(AvatarControl control);

	/**
	 * <strong>For internal use only. To check if a control is pressed, you
	 * should use {@link AvatarControl#isDown()}.</strong>
	 * <p>
	 * Get whether that control is held, only used for non-keybinding
	 * controls
	 */
	boolean isControlDown(AvatarControl control);

	/**
	 * Get the key code for that control. It must be a keybinding.
	 */
	int getKeyCode(AvatarControl control);

	/**
	 * Get the display of that control based on its current binding (adjusts if
	 * the keybinding changes). Null if there is no description available.
	 */
	String getDisplayName(AvatarControl control);

	/**
	 * Get all controls pressed.
	 */
	List<AvatarControl> getAllPressed();

}
