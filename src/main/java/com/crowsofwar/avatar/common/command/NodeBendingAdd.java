package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import crowsofwar.gorecore.tree.ArgumentDirect;
import crowsofwar.gorecore.tree.ArgumentList;
import crowsofwar.gorecore.tree.ArgumentOptions;
import crowsofwar.gorecore.tree.CommandCall;
import crowsofwar.gorecore.tree.IArgument;
import crowsofwar.gorecore.tree.ICommandNode;
import crowsofwar.gorecore.tree.ITypeConverter;
import crowsofwar.gorecore.tree.NodeFunctional;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

public class NodeBendingAdd extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	private final IArgument<IBendingController> argBendingController;
	
	public NodeBendingAdd() {
		super("add", true);
		
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
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(world, playerName, "Error while getting player data for /avatar bending add");
		
		if (data == null) {
			
			MSG_PLAYER_DATA_NO_DATA.send(sender, playerName);
			
		} else {
			
			if (data.hasBending(controller.getID())) {
				
				MSG_BENDING_ADD_ALREADY_HAS.send(sender, playerName, controller.getControllerName());
				
			} else {
				
				data.addBending(controller);
				MSG_BENDING_ADD_SUCCESS.send(sender, playerName, controller.getControllerName());
				
			}
			
		}
		
		return null;
	}
	
}
