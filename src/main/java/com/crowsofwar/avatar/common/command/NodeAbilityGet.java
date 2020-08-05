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

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.tree.*;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public class NodeAbilityGet extends NodeFunctional {

	private final IArgument<String> argPlayer;
	private final IArgument<Ability> argAbility;

	/**
	 * @param name
	 * @param op
	 */
	public NodeAbilityGet() {
		super("get", true);
		argPlayer = addArgument(new ArgumentPlayerName("player"));
		argAbility = addArgument(new ArgumentAbility("ability"));
	}

	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {

		ArgumentList args = call.popArguments(this);
		String player = args.get(argPlayer);
		Ability ability = args.get(argAbility);

		BendingData data = BendingData.get(call.getFrom().getEntityWorld(), player);
		if (data != null) {

			float xp = data.getAbilityData(ability).getTotalXp();
			AvatarChatMessages.MSG_ABILITY_GET.send(call.getFrom(), player, ability.getName(), xp);

		}

		return null;
	}

}
