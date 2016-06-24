package com.maxandnoah.avatar.common;

import static com.maxandnoah.avatar.common.bending.BendingManager.*;

import com.maxandnoah.avatar.common.bending.BendingManager;
import com.maxandnoah.avatar.common.bending.IBendingController;

/**
 * Describes an action which is sent to the server.
 *
 */
public enum AvatarAbility {
	
	/** No control is pressed */
	NONE("", -1),
	ACTION_TOGGLE_BENDING("ToggleBending", BENDINGID_EARTHBENDING),
	ACTION_THROW_BLOCK("ThrowBlock", BENDINGID_EARTHBENDING);
	
	private String name;
	private IBendingController controller;
	
	private AvatarAbility(String name, int compatibleBendingID) {
		this.name = name;
		this.controller = BendingManager.getBending(compatibleBendingID);
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return ordinal();
	}
	
	public IBendingController getBendingController() {
		return controller;
	}
	
	public boolean isCompatible(IBendingController controller) {
		return this.controller == controller;
	}
	
	/**
	 * Get the ability with the specified id.
	 * @see #getId()
	 * @throws IllegalArgumentException ID must be from getId. If not referring to an ability, exception is thrown.
	 */
	public static AvatarAbility fromId(int id) {
		if (id < 0 || id >= values().length) throw new IllegalArgumentException("Invalid ID for avatar ability: " + id);
		return values()[id];
	}
	
}
