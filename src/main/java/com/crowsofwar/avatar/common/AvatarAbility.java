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
	NONE("", -1, -2, false), ACTION_TOGGLE_BENDING("ToggleBending", BENDINGID_EARTHBENDING, -1, false, 0), ACTION_THROW_BLOCK("ThrowBlock", BENDINGID_EARTHBENDING, -2, false), ACTION_LIGHT_FIRE("LightFire", BENDINGID_FIREBENDING, -1, false, 2), ACTION_FIRE_PUNCH("FirePunch", BENDINGID_FIREBENDING, -2, false, 3), ACTION_FIREARC_THROW("FireThrow", BENDINGID_FIREBENDING, -2, false), ACTION_PUT_BLOCK("PutBlock", BENDINGID_EARTHBENDING, -1, false), ACTION_WATER_ARC("WaterArc", BENDINGID_WATERBENDING, -1, true, 4), ACTION_WATERARC_THROW("WaterThrow", BENDINGID_WATERBENDING, -2, false, 5), ACTION_AIRBEND_TEST("AirbendTest", BENDINGID_AIRBENDING, -2, false, 6), ACTION_AIR_GUST("AirGust", BENDINGID_AIRBENDING, -2, false, 7);
	
	private final String name;
	private final IBendingController controller;
	/**
	 * Raytrace distance. -1 = Player's reach, -2 = No raytrace.
	 */
	private final double raytrace;
	
	private final boolean raytraceLiquids;
	/**
	 * The index of the icon found in the ability_icons spritemap. -1 if there is no index
	 */
	private final int iconIndex;
	
	private AvatarAbility(String name, int compatibleBendingId, double raytrace, boolean raytraceLiquids) {
		this(name, compatibleBendingId, raytrace, raytraceLiquids, -1);
	}
	
	private AvatarAbility(String name, int compatibleBendingId, double raytrace, boolean raytraceLiquids, int iconIndex) {
		this.name = name;
		this.controller = BendingManager.getBending(compatibleBendingId);
		this.raytrace = raytrace;
		this.raytraceLiquids = raytraceLiquids;
		this.iconIndex = iconIndex;
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
	
	public boolean isRaycastLiquids() {
		return raytraceLiquids;
	}
	
	public int getIconIndex() {
		return iconIndex;
	}
	
	public boolean hasIcon() {
		return iconIndex != -1;
	}
	
	/**
	 * Get the ability with the specified id.
	 * 
	 * @see #getId()
	 * @throws IllegalArgumentException
	 *             ID must be from getId. If not referring to an ability, exception is thrown.
	 */
	public static AvatarAbility fromId(int id) {
		if (id < 0 || id >= values().length) throw new IllegalArgumentException("Invalid ID for avatar ability: " + id);
		return values()[id];
	}
	
}
