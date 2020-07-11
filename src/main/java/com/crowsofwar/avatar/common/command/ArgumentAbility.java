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
package com.crowsofwar.avatar.common.command;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.TreeCommandException;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Argument for an ability specified by its {@link Ability#getName()
 * internal name}. Will give null if incorrect input
 *
 * @author CrowsOfWar
 */
public class ArgumentAbility implements IArgument<Ability> {

	private final String name;

	public ArgumentAbility(String name) {
		this.name = name;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public Ability getDefaultValue() {
		return null;
	}

	@Override
	public Ability convert(String input) {

		for (Ability ability : Abilities.all()) {
			if (ability.getName().equals(input)) {
				return ability;
			}
		}

		throw new TreeCommandException("avatar.cmd.noAbility", input);

	}

	@Override
	public String getArgumentName() {
		return name;
	}

	@Override
	public String getHelpString() {
		String out = "<";
		for (Ability ability : Abilities.all()) {
			out += ability.getName() + "|";
		}
		return out.substring(0, out.length() - 1) + ">";
	}

	@Override
	public String getSpecificationString() {
		return "<" + name + ">";
	}

	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		List<String> out = new ArrayList<>();
		for (Ability ability : Abilities.all()) {
			out.add(ability.getName());
		}
		return out;
	}

}
