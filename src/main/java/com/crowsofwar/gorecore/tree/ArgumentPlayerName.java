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

package com.crowsofwar.gorecore.tree;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * An argument for a player's username. Supports tab completion if the player is
 * currently in the world. However, players not in the world can be specified.
 * 
 * @author CrowsOfWar
 */
public class ArgumentPlayerName implements IArgument<String> {
	
	private final String name;
	
	public ArgumentPlayerName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isOptional() {
		return false;
	}
	
	@Override
	public String getDefaultValue() {
		return null;
	}
	
	@Override
	public String convert(String input) {
		return input;
	}
	
	@Override
	public String getArgumentName() {
		return name;
	}
	
	@Override
	public String getHelpString() {
		return "<playername>";
	}
	
	@Override
	public String getSpecificationString() {
		return "<" + getArgumentName() + ">";
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		World world = sender.getEntityWorld();
		List<String> suggestions = new ArrayList<>();
		
		world.playerEntities.forEach(player -> {
			suggestions.add(player.getName());
		});
		
		if (!suggestions.get(0).toLowerCase().startsWith(currentInput.toLowerCase()))
			return new ArrayList<>();
		return suggestions;
	}
	
}
