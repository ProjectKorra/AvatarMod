package com.crowsofwar.avatar.common.command;

import crowsofwar.gorecore.tree.ICommandNode;
import crowsofwar.gorecore.tree.TreeCommand;

public class AvatarCommand extends TreeCommand {
	
	@Override
	public String getCommandName() {
		return "avatar";
	}
	
	@Override
	protected ICommandNode[] addCommands() {
		return null;
	}
	
}
