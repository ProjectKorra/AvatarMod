package com.crowsofwar.avatar.common;

import static com.crowsofwar.avatar.common.bending.BendingManager.*;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingController;

/**
 * Describes an action which is sent to the server.
 *
 */
public enum AvatarAbility {
	
	/** No control is pressed */
	NONE("", -1, -2),
	ACTION_TOGGLE_BENDING("ToggleBending", BENDINGID_EARTHBENDING, -1),
	ACTION_THROW_BLOCK("ThrowBlock", BENDINGID_EARTHBENDING, -2),
	ACTION_LIGHT_FIRE("LightFire", BENDINGID_FIREBENDING, -1);
	
	private String name;
	private IBendingController controller;
	/**
	 * Raytrace distance. -1 = Player's reach, -2 = No raytrace.
	 */
	private double raytrace;
	
	private AvatarAbility(String name, int compatibleBendingID, double raytrace) {
		this.name = name;
		this.controller = BendingManager.getBending(compatibleBendingID);
		this.raytrace = raytrace;
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
	
	public boolean needsRaytrace() {
		return raytrace != -2;
	}
	
	public double getRaytraceDistance() {
		return raytrace;
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
