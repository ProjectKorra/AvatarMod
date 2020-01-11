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
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.event.ElementRemoveEvent;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.gorecore.tree.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

public class NodeBendingRemove extends NodeFunctional {

	private final IArgument<String> argPlayerName;
	private final IArgument<List<BendingStyle>> argBendingController;

	public NodeBendingRemove() {
		super("remove", true);

		this.argPlayerName = new ArgumentPlayerName("player");
		this.argBendingController = new ArgumentOptions<>(
				AvatarCommand.CONVERTER_BENDING, "bending", AvatarCommand.CONTROLLER_BENDING_OPTIONS);

		this.addArgument(argPlayerName);
		this.addArgument(argBendingController);

	}

	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {

		ICommandSender sender = call.getFrom();
		World world = sender.getEntityWorld();

		ArgumentList args = call.popArguments(this);

		String playerName = args.get(argPlayerName);
		List<BendingStyle> controllers = args.get(argBendingController);
		EntityPlayer player = AvatarEntityUtils.getPlayerFromUsername(playerName);

		for (BendingStyle controller : controllers) {
			BendingData data = BendingData.get(world, playerName);
			if (controller.canEntityUse()) {
				if (data == null) {
					MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);
				} else {

					if (data.hasBending(controller)) {
						if (!MinecraftForge.EVENT_BUS.post(new ElementRemoveEvent(player, controller))) {
							data.removeBending(controller);
							MSG_BENDING_REMOVE_SUCCESS.send(sender, playerName, controller.getName());
						}
					} else {
						MSG_BENDING_REMOVE_DOESNT_HAVE.send(sender, playerName, controller.getName());
					}
				}
			}
		}

		return null;
	}

}
