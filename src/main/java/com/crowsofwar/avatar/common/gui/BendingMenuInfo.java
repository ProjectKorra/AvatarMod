package com.crowsofwar.avatar.common.gui;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;

/**
 * Encapsulates information about an BendingController's radial menu- the control and
 * AvatarAbilities which will be included in the Gui.
 *
 */
public class BendingMenuInfo {
	
	private MenuTheme theme;
	private final AvatarControl key;
	private final BendingAbility[] buttons;
	
	/**
	 * Create information for an BendingController's radial menu.
	 * 
	 * @param theme
	 *            The theme of this menu, defines colors, etc.
	 * @param key
	 *            The key which must be held to use this radial menu
	 * @param buttons
	 *            An array of abilities which will be used as the buttons. Can't be more than 8. If
	 *            it is less than 8, the unspecified elements are filled with
	 *            {@link AvatarAbility#NONE}.
	 */
	public BendingMenuInfo(MenuTheme theme, AvatarControl key, BendingAbility... buttons) {
		if (buttons.length > 8) throw new IllegalArgumentException(
				"Cannot create BendingMenuInfo with buttons being larger than 8");
		this.theme = theme;
		this.key = key;
		this.buttons = new BendingAbility[8];
		for (int i = 0; i < 8; i++) {
			this.buttons[i] = i < buttons.length ? buttons[i] : null;
		}
	}
	
	public MenuTheme getTheme() {
		return theme;
	}
	
	public AvatarControl getKey() {
		return key;
	}
	
	/**
	 * Get all the buttons. Size is guaranteed to be 8; if there is no button in that slot, it is
	 * null.
	 */
	public BendingAbility[] getButtons() {
		return buttons;
	}
	
}
