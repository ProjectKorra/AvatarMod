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

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates information about an BendingController's radial menu- the
 * control and AvatarAbilities which will be included in the Gui.
 */
public class BendingMenuInfo {

	private final Ability[] buttons;
	private MenuTheme theme;

	/**
	 * Create information for an BendingController's radial menu.
	 *
	 * @param theme The theme of this menu, defines colors, etc.
	 */
	public BendingMenuInfo(MenuTheme theme, BendingStyle bending) {

		List<Ability> buttons = bending.getAllAbilities();

		// Filter out invisible abilities
		buttons = buttons.stream().filter(Ability::isVisibleInRadial).collect(Collectors.toList());

		if (buttons.size() > 8) throw new IllegalArgumentException(
				"Cannot get BendingMenuInfo with buttons being larger than 8");

		this.theme = theme;
		this.buttons = new Ability[8];
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
	public Ability[] getButtons() {
		return buttons;
	}

}
