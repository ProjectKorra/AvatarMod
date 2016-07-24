package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import crowsofwar.gorecore.tree.ArgumentDirect;
import crowsofwar.gorecore.tree.ArgumentList;
import crowsofwar.gorecore.tree.CommandCall;
import crowsofwar.gorecore.tree.IArgument;
import crowsofwar.gorecore.tree.ICommandNode;
import crowsofwar.gorecore.tree.ITypeConverter;
import crowsofwar.gorecore.tree.NodeFunctional;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class NodeBendingList extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	
	public NodeBendingList() {
		super("list", true);
		this.argPlayerName = new ArgumentDirect<String>("player", ITypeConverter.CONVERTER_STRING);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ICommandSender sender = call.getFrom();
		World world = sender.getEntityWorld();
		
		ArgumentList args = call.popArguments(argPlayerName);
		String playerName = args.get(argPlayerName);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(world, playerName, "Retrieving for /avatar bending list");
		if (data == null) {
			
			AvatarChatMessages.MSG_BENDING_LIST_NO_DATA.send(sender, playerName);
			
		} else {
			
			if (data.isBender()) {
				
				List<IBendingController> allControllers = data.getBendingControllers();
				AvatarChatMessages.MSG_BENDING_LIST_TOP.send(sender, playerName, allControllers.size());
				
				for (IBendingController controller : allControllers) {
					AvatarChatMessages.MSG_BENDING_LIST_ITEM.send(sender, controller.getControllerName());
				}
				
			} else {
				
				AvatarChatMessages.MSG_BENDING_LIST_NONBENDER.send(sender, playerName);
				
			}
			
		}
		
		return null;
	}
	
}
