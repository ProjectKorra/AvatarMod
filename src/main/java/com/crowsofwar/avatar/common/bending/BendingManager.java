package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages instances of bending controllers. Bending controllers can be retrieved via
 * {@link #getBending(int)}. Contains constants which specify the IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(IBendingController)} to enable custom bending
 * controllers.
 *
 */
public class BendingManager {
	
	public static final int BENDINGID_EARTHBENDING = 1;
	public static final int BENDINGID_FIREBENDING = 2;
	public static final int BENDINGID_WATERBENDING = 3;
	public static final int BENDINGID_AIRBENDING = 4;
	
	private static Map<Integer, IBendingController> bending;
	private static Map<String, IBendingController> bendingByName;
	private static List<IBendingController> allBending;
	
	public static void init() {
		bending = new HashMap<Integer, IBendingController>();
		bendingByName = new HashMap<String, IBendingController>();
		allBending = new ArrayList<IBendingController>();
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
	public static IBendingController getBending(int id) {
		return bending.get(id);
	}
	
	/**
	 * Get the BendingController with the given name. Returns null if the name is invalid.
	 * 
	 * @param name
	 *            The name of the bending controller
	 * @return
	 */
	public static IBendingController getBending(String name) {
		return bendingByName.get(name);
	}
	
	/**
	 * Get a list of all bending controllers. This cannot be modified.
	 * 
	 * @return
	 */
	public static List<IBendingController> allBending() {
		return Collections.unmodifiableList(allBending);
	}
	
	public static void registerBending(IBendingController controller) {
		bending.put(controller.getID(), controller);
		bendingByName.put(controller.getControllerName(), controller);
		allBending.add(controller);
	}
	
}
