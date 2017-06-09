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

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeXpSet extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	private final IArgument<BendingAbility> argAbility;
	private final IArgument<String> argSpecification;
	private final IArgument<Float> argNewXp;
	
	public NodeXpSet() {
		super("xp", true);
		
		argPlayerName = new ArgumentPlayerName("player");
		argAbility = new ArgumentAbility("ability");
		argSpecification = new ArgumentOptions<>(ITypeConverter.CONVERTER_STRING, "specification", //
				"locked", "lvl1", "lvl2", "lvl3", "lvl4_1", "lvl4_2");
		argNewXp = new ArgumentDirect<>("xp", ITypeConverter.CONVERTER_FLOAT, 0f);
		
		addArguments(argPlayerName, argAbility, argSpecification, argNewXp);
		
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ArgumentList args = call.popArguments(this);
		String playerName = args.get(argPlayerName);
		BendingAbility ability = args.get(argAbility);
		String specification = args.get(argSpecification);
		float xp = args.get(argNewXp);
		
		BendingData data = AvatarPlayerData.fetcher().fetch(call.getFrom().getEntityWorld(), playerName);
		AbilityData abilityData = data.getAbilityData(ability);
		
		int level;
		AbilityTreePath path = AbilityTreePath.MAIN;
		
		if (specification.equals("locked")) {
			level = -1;
			xp = 0;
		} else {
			String secondPart = specification.substring("lvl".length());
			level = Integer.parseInt(secondPart.charAt(0) + "") - 1;
			
			if (level == 3) {
				String pathStr = secondPart.substring("n_".length());
				int index = Integer.parseInt(pathStr);
				path = AbilityTreePath.values()[index];
			}
			
		}
		
		System.out.printf("Parsed to: level %d, xp %f, path %s", level, xp, path);
		abilityData.setLevel(level);
		abilityData.setXp(xp);
		abilityData.setPath(path);
		
		return null;
		
	}
	
}
