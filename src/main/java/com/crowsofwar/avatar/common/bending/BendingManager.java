/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.ice.Icebending;
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;

/**
 * Manages instances of bending controllers. Bending controllers can be
 * retrieved via {@link #getBending(int)}. Contains constants which specify the
 * IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(BendingStyle)} to enable
 * custom bending controllers.
 *
 */
public class BendingManager {
	
	// @formatter:off
	public static final int
		ID_EARTHBENDING = 1,
		ID_FIREBENDING = 2,
		ID_WATERBENDING = 3,
		ID_AIRBENDING = 4,
		ID_LIGHTNINGBENDING = 5,
		ID_ICEBENDING = 6;
	// @formatter:on
	
	private static Map<Integer, BendingStyle> bending;
	private static Map<String, BendingStyle> bendingByName;
	private static List<BendingStyle> allBending;
	
	static {
		bending = new HashMap<>();
		bendingByName = new HashMap<>();
		allBending = new ArrayList<>();
	}
	
	/**
	 * Register all bending controllers. Initialization of the BendingManager is
	 * done in a static block. Requires BendingAbilities to be created.
	 */
	public static void init() {
		registerBending(new Earthbending());
		registerBending(new Firebending());
		registerBending(new Waterbending());
		registerBending(new Airbending());
		registerBending(new Lightningbending());
		registerBending(new Icebending());
	}
	
	/**
	 * Get the BendingController for that bending type.
	 * 
	 * @param id
	 *            Bending type to look for
	 * @throws IllegalArgumentException
	 *             If no bending controller for that type (shouldn't happen)
	 */
	public static BendingStyle getBending(int id) {
		if (!bending.containsKey(id))
			throw new IllegalArgumentException("No bending controller with type " + id);
		return bending.get(id);
	}
	
	/**
	 * Get the BendingController with the given name. Returns null if the name
	 * is invalid.
	 * 
	 * @param name
	 *            The name of the bending controller
	 */
	public static BendingStyle getBending(String name) {
		return bendingByName.get(name);
	}
	
	/**
	 * Check whether a BendingController under that ID exists.
	 */
	public static boolean hasBending(int id) {
		return bending.containsKey(id);
	}
	
	/**
	 * Get an unmodifiable list of all bending controllers.
	 */
	public static List<BendingStyle> allBending() {
		return Collections.unmodifiableList(allBending);
	}
	
	public static List<Integer> allBendingIds() {
		return allBending.stream().map(b -> b.getId()).collect(Collectors.toList());
	}
	
	/**
	 * Get the id of the controller. 0 on error
	 */
	public static int getControllerId(BendingStyle controller) {
		return controller.getId();
	}
	
	public static void registerBending(BendingStyle controller) {
		bending.put(controller.getId(), controller);
		bendingByName.put(controller.getName(), controller);
		allBending.add(controller);
	}
	
}
