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

import com.crowsofwar.avatar.common.data.Bender;
import net.minecraft.entity.EntityLiving;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author CrowsOfWar
 */
public class Abilities {

	private static final List<Ability> abilities = new ArrayList<>();
	private static final Map<String, Ability> abilitiesByName = new HashMap<>();

	@Nullable
	public static Ability get(String name) {
		return abilitiesByName.get(name);
	}

	@Nullable
	public static BendingAi getAi(String name, EntityLiving entity, Bender bender) {
		Ability ability = get(name);
		if (ability != null) {
			return ability.getAi(entity, bender);
		} else {
			return null;
		}
	}

	public static List<Ability> all() {
		return abilities;
	}

	public static void register(Ability ability) {
		abilities.add(ability);
		abilitiesByName.put(ability.getName(), ability);
	}

	//Gets a list of abilities to add in the radial menu based on an element.
	//You'll probably want to fix this in the future, but it works for now.
	public static List<Ability> getAbilitiesToRegister(UUID element) {
		List<Ability> abilityList = abilities;
		List<Ability> toRemove = new ArrayList<>();
		ArrayList<Ability> elementAbilities = new ArrayList<>();
		//This is to make sure the elementAbilities list is correct.
		ArrayList<Ability> tempList = new ArrayList<>();

		Ability previousAbility;
		int prevTier = 1, index = 0;

		//This auto-registers the abilities based on tier. Yay!
		if (!abilityList.isEmpty()) {
			for (Ability a : abilityList) {
				if (a.getBendingId() != element)
					toRemove.add(a);
			}
			abilityList.removeAll(toRemove);
			for (Ability ability : abilityList) {
				if (ability.getBaseTier() > prevTier || ability.getBaseTier() == prevTier) {
					elementAbilities.add(index, ability);
					index++;
				} else {
					//Moves the previous ability ahead in the list.
					previousAbility = elementAbilities.get(Math.max(index - 1, 0));
					elementAbilities.add(index, previousAbility);
				//	elementAbilities.remove(previousAbility);
					elementAbilities.set(Math.max(index - 1, 0), ability);
					index++;
				}
				prevTier = ability.getBaseTier();
			}
		}
		abilityList.clear();
	/*	tempList = elementAbilities;
		//Resets the previous tier. This double checks and makes sure the abilities are properly registered.
		prevTier = 1;
		index = 0;
		if (!tempList.isEmpty()) {
			for (Ability ability : tempList) {
				if (ability.getBaseTier() > prevTier || ability.getBaseTier() == prevTier) {
					elementAbilities.set(index, ability);
					index++;
				} else {
					//Moves the previous ability ahead in the list.
					previousAbility = elementAbilities.get(Math.max(index - 1, 0));
					elementAbilities.set(index, previousAbility);
					//elementAbilities.remove(previousAbility);
					elementAbilities.set(Math.max(index - 1, 0), ability);
					index++;
				}
				prevTier = ability.getBaseTier();
			}
			tempList.clear();
		}**/
			return elementAbilities;

	}

}
