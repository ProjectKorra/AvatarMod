package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;

/**
 * Manages instances of bending controllers. Bending controllers can be retrieved via
 * {@link #getBending(BendingType)}. Contains constants which specify the IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(BendingController)} to enable custom bending
 * controllers.
 *
 */
public class BendingManager {
	
	/**
	 * Use {@link BendingType} instead.
	 */
	@Deprecated
	public static final int BENDINGID_EARTHBENDING = 1, BENDINGID_FIREBENDING = 2, BENDINGID_WATERBENDING = 3,
			BENDINGID_AIRBENDING = 4;
	
	private static Map<BendingType, BendingController> bending;
	private static Map<String, BendingController> bendingByName;
	private static List<BendingController> allBending;
	
	private static Map<Integer, BendingAbility> abilities;
	private static List<BendingAbility> allAbilities;
	
	public static void init() {
		bending = new HashMap<BendingType, BendingController>();
		bendingByName = new HashMap<String, BendingController>();
		allBending = new ArrayList<BendingController>();
		abilities = new HashMap<>();
		allAbilities = new ArrayList<>();
		registerBending(new Earthbending());
		registerBending(new Firebending());
		registerBending(new Waterbending());
		registerBending(new Airbending());
	}
	
	/**
	 * @deprecated Use {@link #getBending(BendingType)} instead.
	 */
	@Deprecated
	public static BendingController getBending(int id) {
		return bending.get(BendingType.find(id));
	}
	
	/**
	 * Get the BendingController for that bending type. Returns null if invalid.
	 * 
	 * @param type
	 *            Bending type to look for
	 */
	public static BendingController<?> getBending(BendingType type) {
		return bending.get(type);
	}
	
	/**
	 * Get the BendingController with the given name. Returns null if the name is invalid.
	 * 
	 * @param name
	 *            The name of the bending controller
	 */
	public static BendingController getBending(String name) {
		return bendingByName.get(name);
	}
	
	/**
	 * Get an unmodifiable list of all bending controllers.
	 */
	public static List<BendingController> allBending() {
		return Collections.unmodifiableList(allBending);
	}
	
	/**
	 * Get the ability with the given Id, or null if the ability is undefined.
	 * 
	 * @param id
	 *            The Id of the ability
	 */
	public static BendingAbility getAbility(int id) {
		return abilities.get(id);
	}
	
	/**
	 * Returns an unmodifiable view of all abilities.
	 */
	public static List<BendingAbility> allAbilities() {
		return Collections.unmodifiableList(allAbilities);
	}
	
	public static void registerBending(BendingController controller) {
		bending.put(controller.getType(), controller);
		bendingByName.put(controller.getControllerName(), controller);
		allBending.add(controller);
	}
	
	public static void registerAbility(BendingAbility ability) {
		abilities.put(ability.getId(), ability);
		allAbilities.add(ability);
	}
	
}
