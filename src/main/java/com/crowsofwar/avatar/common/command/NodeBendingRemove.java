package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import crowsofwar.gorecore.tree.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

public class NodeBendingRemove extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	private final IArgument<IBendingController> argBendingController;
	
	public NodeBendingRemove() {
		super("remove", true);
		
		this.argPlayerName = new ArgumentDirect<String>("player", ITypeConverter.CONVERTER_STRING);
		this.argBendingController = new ArgumentOptions<IBendingController>(AvatarCommand.CONVERTER_BENDING, "bending",
				BendingManager.allBending().toArray(new IBendingController[0]));
		
		this.addArguments(argPlayerName, argBendingController);
		
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ICommandSender sender = call.getFrom();
		World world = sender.getEntityWorld();
		
		ArgumentList args = call.popArguments(argPlayerName, argBendingController);
		
		String playerName = args.get(argPlayerName);
		IBendingController controller = args.get(argBendingController);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(world, playerName, "Error while getting player data for /avatar bending remove");
		if (data == null) {
			
			MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);
			
		} else {
			
			if (data.hasBending(controller.getID())) {
				
				data.removeBending(controller);
				MSG_BENDING_REMOVE_SUCCESS.send(sender, playerName, controller.getControllerName());
				
			} else {
				
				MSG_BENDING_REMOVE_DOESNT_HAVE.send(sender, playerName, controller.getControllerName());
				
			}
			
		}
		
		return null;
	}
	
}
