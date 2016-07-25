package com.crowsofwar.avatar.common.bending;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages instances of bending controllers. Bending controllers
 * can be retrieved via {@link #getBending(int)}. Contains constants
 * which specify the IDs of bending.
 * <br /><br />
 * Third-party mods can use {@link #registerBending(IBendingController)}
 * to enable custom bending controllers.
 *
 */
public class BendingManager {
	
	public static final int BENDINGID_EARTHBENDING = 1;
	public static final int BENDINGID_FIREBENDING = 2;
	public static final int BENDINGID_WATERBENDING = 3;
	
	private static Map<Integer, IBendingController> bending;
	private static Map<String, IBendingController> bendingByName;
	
	public static void init() {
		bending = new HashMap<Integer, IBendingController>();
		bendingByName = new HashMap<String, IBendingController>();
		registerBending(new Earthbending());
		registerBending(new Firebending());
		registerBending(new Waterbending());
	}
	
	/**
	 * Get the BendingController with that ID. Returns null if the
	 * given Id is invalid.
	 * @param id
	 * @return
	 */
	public static IBendingController getBending(int id) {
		return bending.get(id);
	}
	
	public static void registerBending(IBendingController controller) {
		bending.put(controller.getID(), controller);
		bendingByName.put(controller.getControllerName(), controller);
	}
	
}
