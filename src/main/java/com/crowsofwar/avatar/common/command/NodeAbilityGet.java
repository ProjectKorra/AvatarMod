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

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;
import com.crowsofwar.gorecore.tree.TreeCommandException;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeAbilityGet extends NodeFunctional {
	
	static final ITypeConverter<BendingAbility> convertAbility = new ITypeConverter<BendingAbility>() {
		@Override
		public BendingAbility convert(String str) {
			List<BendingAbility> allAbilities = BendingManager.allAbilities();
			for (BendingAbility ability : allAbilities) {
				if (ability.getName().equals(str.toLowerCase())) return ability;
			}
			
			throw new TreeCommandException(Reason.NOT_OPTION);
		}
		
		@Override
		public String toString(BendingAbility obj) {
			return obj.getName();
		}
		
		@Override
		public String getTypeName() {
			return "Ability";
		}
	};
	
	static final BendingAbility[] allAbilities;
	static {
		List<BendingAbility> list = BendingManager.allAbilities();
		allAbilities = new BendingAbility[list.size()];
		for (int i = 0; i < list.size(); i++)
			allAbilities[i] = list.get(i);
	}
	
	private final IArgument<String> argPlayer;
	private final IArgument<BendingAbility> argAbility;
	
	/**
	 * @param name
	 * @param op
	 */
	public NodeAbilityGet() {
		super("get", true);
		argPlayer = addArgument(new ArgumentPlayerName("player"));
		argAbility = addArgument(new ArgumentOptions<>(convertAbility, "ability", allAbilities));
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ArgumentList args = call.popArguments(this);
		String player = args.get(argPlayer);
		BendingAbility ability = args.get(argAbility);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(call.getFrom().getEntityWorld(), player);
		if (data != null) {
			
			float xp = data.getAbilityData(ability).getXp();
			AvatarChatMessages.MSG_ABILITY_GET.send(call.getFrom(), player, ability.getName(), xp);
			
		}
		
		return null;
	}
	
}
