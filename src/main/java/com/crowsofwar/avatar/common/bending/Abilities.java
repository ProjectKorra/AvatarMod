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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Abilities {
	
	private static final List<Ability> abilities = new ArrayList<>();
	private static final Map<UUID, Ability> abilitiesById = new HashMap<>();
	private static final Map<String, Ability> abilitiesByName = new HashMap<>();
	
	@Nullable
	public static Ability get(UUID id) {
		return abilitiesById.get(id);
	}
	
	@Nullable
	public static Ability get(String name) {
		return abilitiesByName.get(name);
	}
	
	public static List<Ability> all() {
		return abilities;
	}
	
	public static void register(Ability ability) {
		abilities.add(ability);
		abilitiesById.put(ability.getId(), ability);
		abilitiesByName.put(ability.getName(), ability);
	}
	
}
