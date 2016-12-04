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
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class NodeBendingList extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	
	public NodeBendingList() {
		super("list", true);
		this.argPlayerName = new ArgumentPlayerName("player");
		addArgument(argPlayerName);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ICommandSender sender = call.getFrom();
		World world = sender.getEntityWorld();
		
		ArgumentList args = call.popArguments(this);
		String playerName = args.get(argPlayerName);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(world, playerName);
		if (data == null) {
			
			AvatarChatMessages.MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);
			
		} else {
			
			if (data.isBender()) {
				
				List<BendingController> allControllers = data.getBendingControllers();
				AvatarChatMessages.MSG_BENDING_LIST_TOP.send(sender, playerName, allControllers.size());
				
				for (BendingController controller : allControllers) {
					AvatarChatMessages.MSG_BENDING_LIST_ITEM.send(sender, controller.getControllerName());
				}
				
			} else {
				
				AvatarChatMessages.MSG_BENDING_LIST_NONBENDER.send(sender, playerName);
				
			}
			
		}
		
		return null;
	}
	
}
