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
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;

/**
 * Manages instances of bending controllers. Bending controllers can be
 * retrieved via {@link #getBending(int)}. Contains constants which specify the
 * IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(BendingController)} to
 * enable custom bending controllers.
 *
 */
public class BendingManager {
	
	// @formatter:off
	public static int
		ID_EARTHBENDING,
		ID_FIREBENDING,
		ID_WATERBENDING,
		ID_AIRBENDING,
		ID_LIGHTNINGBENDING;
	// @formatter:on
	
	private static Map<Integer, BendingController> bending;
	private static Map<String, BendingController> bendingByName;
	private static List<BendingController> allBending;
	private static Map<BendingController, Integer> bendingIds;
	
	private static Map<Integer, BendingAbility> abilities;
	private static List<BendingAbility> allAbilities;
	
	private static int nextId = 1;
	
	static {
		bending = new HashMap<>();
		bendingByName = new HashMap<>();
		allBending = new ArrayList<>();
		bendingIds = new HashMap<>();
		abilities = new HashMap<>();
		allAbilities = new ArrayList<>();
	}
	
	/**
	 * Register all bending controllers. Initialization of the BendingManager is
	 * done in a static block. Requires BendingAbilities to be created.
	 */
	public static void init() {
		ID_EARTHBENDING = registerBending(new Earthbending());
		ID_FIREBENDING = registerBending(new Firebending());
		ID_WATERBENDING = registerBending(new Waterbending());
		ID_AIRBENDING = registerBending(new Airbending());
		ID_LIGHTNINGBENDING = registerBending(new Lightningbending());
	}
	
	/**
	 * Get the BendingController for that bending type.
	 * 
	 * @param id
	 *            Bending type to look for
	 * @throws IllegalArgumentException
	 *             If no bending controller for that type (shouldn't happen)
	 */
	public static BendingController getBending(int id) {
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
	public static BendingController getBending(String name) {
		return bendingByName.get(name);
	}
	
	/**
	 * Get an unmodifiable list of all bending controllers.
	 */
	public static List<BendingController> allBending() {
		return Collections.unmodifiableList(allBending);
	}
	
	public static List<Integer> allBendingIds() {
		return allBending.stream().map(b -> b.getId()).collect(Collectors.toList());
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
	 * Get the id of the controller. 0 on error
	 */
	public static int getControllerId(BendingController controller) {
		return bendingIds.get(controller) == null ? 0 : bendingIds.get(controller);
	}
	
	/**
	 * Returns an unmodifiable view of all abilities.
	 */
	public static List<BendingAbility> allAbilities() {
		return Collections.unmodifiableList(allAbilities);
	}
	
	public static int registerBending(BendingController controller) {
		bending.put(nextId, controller);
		bendingByName.put(controller.getName(), controller);
		allBending.add(controller);
		bendingIds.put(controller, nextId);
		
		// Avoid "return nextId++" since that's less readable
		int id = nextId;
		nextId++;
		return id;
		
	}
	
	public static void registerAbility(BendingAbility ability) {
		abilities.put(ability.getId(), ability);
		allAbilities.add(ability);
	}
	
}
