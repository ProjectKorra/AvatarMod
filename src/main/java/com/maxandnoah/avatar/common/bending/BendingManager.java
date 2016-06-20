package com.maxandnoah.avatar.common.bending;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages instances of bending controllers. Bending controllers
 * can be retrieved via {@link #getBending(int)}. Contains constants
 * which specify the IDs of bending.
 * <br /><br />
 * Third-party mods can use {@link #registerBending(BendingController)}
 * to enable custom bending controllers.
 *
 */
public class BendingManager {
	
	public static final int BENDINGID_EARTHBENDING = 1;
	
	private static Map<Integer, BendingController> bending;
	
	public static void init() {
		bending = new HashMap<Integer, BendingController>();
		registerBending(new Earthbending());
	}
	
	/**
	 * Get the BendingController with that ID.
	 * @param id
	 * @return
	 */
	public static BendingController getBending(int id) {
		return bending.get(id);
	}
	
	public static void registerBending(BendingController controller) {
		bending.put(controller.getID(), controller);
	}
	
}
