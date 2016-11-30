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

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class NodeBendingRemove extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	private final IArgument<BendingController> argBendingController;
	
	public NodeBendingRemove() {
		super("remove", true);
		
		this.argPlayerName = new ArgumentPlayerName("player");
		this.argBendingController = new ArgumentOptions<BendingController>(AvatarCommand.CONVERTER_BENDING,
				"bending", BendingManager.allBending().toArray(new BendingController[0]));
		
		this.addArgument(argPlayerName);
		this.addArgument(argBendingController);
		
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ICommandSender sender = call.getFrom();
		World world = sender.getEntityWorld();
		
		ArgumentList args = call.popArguments(this);
		
		String playerName = args.get(argPlayerName);
		BendingController controller = args.get(argBendingController);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(world, playerName);
		if (data == null) {
			
			MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);
			
		} else {
			
			if (data.hasBending(controller.getID())) {
				
				data.removeBending(controller);
				data.sync();
				MSG_BENDING_REMOVE_SUCCESS.send(sender, playerName, controller.getControllerName());
				
			} else {
				
				MSG_BENDING_REMOVE_DOESNT_HAVE.send(sender, playerName, controller.getControllerName());
				
			}
			
		}
		
		return null;
	}
	
}
