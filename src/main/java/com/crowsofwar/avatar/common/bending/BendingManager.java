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
 * {@link #getBending(int)}. Contains constants which specify the IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(BendingController)} to enable custom bending
 * controllers.
 *
 */
public class BendingManager {
	
	public static final int BENDINGID_EARTHBENDING = 1;
	public static final int BENDINGID_FIREBENDING = 2;
	public static final int BENDINGID_WATERBENDING = 3;
	public static final int BENDINGID_AIRBENDING = 4;
	
	private static Map<Integer, BendingController> bending;
	private static Map<String, BendingController> bendingByName;
	private static List<BendingController> allBending;
	
	private static Map<Integer, BendingAbility> abilities;
	private static List<BendingAbility> allAbilities;
	
	public static void init() {
		bending = new HashMap<Integer, BendingController>();
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
	 * Get the BendingController with that ID. Returns null if the given Id is invalid.
	 * 
	 * @param id
	 * @return
	 */
	public static BendingController getBending(int id) {
		return bending.get(id);
	}
	
	/**
	 * Get the BendingController with the given name. Returns null if the name is invalid.
	 * 
	 * @param name
	 *            The name of the bending controller
	 * @return
	 */
	public static BendingController getBending(String name) {
		return bendingByName.get(name);
	}
	
	/**
	 * Get a list of all bending controllers. This cannot be modified.
	 * 
	 * @return
	 */
	public static List<BendingController> allBending() {
		return Collections.unmodifiableList(allBending);
	}
	
	public static BendingAbility getAbility(int id) {
		return abilities.get(id);
	}
	
	public static List<BendingAbility> allAbilities() {
		return Collections.unmodifiableList(allAbilities);
	}
	
	public static void registerBending(BendingController controller) {
		bending.put(controller.getID(), controller);
		bendingByName.put(controller.getControllerName(), controller);
		allBending.add(controller);
	}
	
	public static void registerAbility(BendingAbility ability) {
		abilities.put(ability.getId(), ability);
		allAbilities.add(ability);
	}
	
}
