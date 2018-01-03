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
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.tree.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

import java.util.List;

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

		BendingData data = BendingData.get(world, playerName);
		if (data == null) {

			AvatarChatMessages.MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);

		} else {

			if (!data.getAllBending().isEmpty()) {

				List<BendingStyle> allControllers = data.getAllBending();
				AvatarChatMessages.MSG_BENDING_LIST_TOP.send(sender, playerName, allControllers.size());

				for (BendingStyle controller : allControllers) {
					AvatarChatMessages.MSG_BENDING_LIST_ITEM.send(sender, controller.getName());
				}

			} else {

				AvatarChatMessages.MSG_BENDING_LIST_NONBENDER.send(sender, playerName);

			}

		}

		return null;
	}

}
