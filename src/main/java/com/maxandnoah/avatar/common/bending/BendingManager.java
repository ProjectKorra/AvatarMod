package com.maxandnoah.avatar.common.bending;

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
	
	private static Map<Integer, IBendingController> bending;
	
	public static void init() {
		bending = new HashMap<Integer, IBendingController>();
		registerBending(new Earthbending());
	}
	
	/**
	 * Get the BendingController with that ID.
	 * @param id
	 * @return
	 */
	public static IBendingController getBending(int id) {
		return bending.get(id);
	}
	
	public static void registerBending(IBendingController controller) {
		bending.put(controller.getID(), controller);
	}
	
}
