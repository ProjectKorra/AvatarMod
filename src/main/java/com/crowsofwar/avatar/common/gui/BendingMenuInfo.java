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

package com.crowsofwar.avatar.common.gui;

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;

/**
 * Encapsulates information about an BendingController's radial menu- the
 * control and AvatarAbilities which will be included in the Gui.
 *
 */
public class BendingMenuInfo {
	
	private MenuTheme theme;
	private final BendingAbility[] buttons;
	
	/**
	 * Create information for an BendingController's radial menu.
	 * 
	 * @param theme
	 *            The theme of this menu, defines colors, etc.
	 * @param buttons
	 *            An array of abilities which will be used as the buttons. Can't
	 *            be more than 8. If it is less than 8, the unspecified elements
	 *            are filled with {@link AvatarAbility#NONE}.
	 */
	public BendingMenuInfo(MenuTheme theme, BendingController bending) {
		List<BendingAbility> buttons = bending.getAllAbilities();
		if (buttons.size() > 8) throw new IllegalArgumentException(
				"Cannot create BendingMenuInfo with buttons being larger than 8");
		this.theme = theme;
		this.buttons = new BendingAbility[8];
		for (int i = 0; i < 8; i++)
			this.buttons[i] = i < buttons.size() ? buttons.get(i) : null;
	}
	
	public MenuTheme getTheme() {
		return theme;
	}
	
	/**
	 * Get all the buttons. Size is guaranteed to be 8; if there is no button in
	 * that slot, it is null.
	 */
	public BendingAbility[] getButtons() {
		return buttons;
	}
	
}
