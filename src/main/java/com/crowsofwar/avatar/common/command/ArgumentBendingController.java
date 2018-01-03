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

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.gorecore.tree.IArgument;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CrowsOfWar
 */
public class ArgumentBendingController implements IArgument<BendingStyle> {

	private final String name;

	public ArgumentBendingController(String name) {
		this.name = name;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public BendingStyle getDefaultValue() {
		return null;
	}

	@Override
	public BendingStyle convert(String input) {
		return BendingStyles.get(input.toLowerCase());
	}

	@Override
	public String getArgumentName() {
		return name;
	}

	@Override
	public String getHelpString() {
		String out = "<";
		for (BendingStyle bc : BendingStyles.all()) {
			out += bc.getName() + "|";
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
		for (BendingStyle bc : BendingStyles.all())
			out.add(bc.getName());
		return out;
	}

}
