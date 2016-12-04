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

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;

/**
 * Manages instances of bending controllers. Bending controllers can be
 * retrieved via {@link #getBending(BendingType)}. Contains constants which
 * specify the IDs of bending. <br />
 * <br />
 * Third-party mods can use {@link #registerBending(BendingController)} to
 * enable custom bending controllers.
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
	
	static {
		bending = new HashMap<BendingType, BendingController>();
		bendingByName = new HashMap<String, BendingController>();
		allBending = new ArrayList<BendingController>();
		abilities = new HashMap<>();
		allAbilities = new ArrayList<>();
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
	}
	
	/**
	 * @deprecated Use {@link #getBending(BendingType)} instead.
	 * @throws IllegalArgumentException
	 *             if there is no bending with the ID
	 */
	@Deprecated
	public static BendingController getBending(int id) {
		return bending.get(BendingType.find(id));
	}
	
	/**
	 * Get the BendingController for that bending type.
	 * 
	 * @param type
	 *            Bending type to look for
	 * @throws IllegalArgumentException
	 *             If no bending controller for that type (shouldn't happen)
	 */
	public static BendingController getBending(BendingType type) {
		if (!bending.containsKey(type)) throw new IllegalArgumentException(
				"No bending controller with type " + type + "... devs forgot to add a bending controller!");
		return bending.get(type);
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
