package com.crowsofwar.avatar.common.command;

import com.crowsofwar.avatar.common.AvatarChatMessages;

import crowsofwar.gorecore.tree.ICommandNode;
import crowsofwar.gorecore.tree.NodeBranch;
import crowsofwar.gorecore.tree.TreeCommand;

public class AvatarCommand extends TreeCommand {
	
	@Override
	public String getCommandName() {
		return "avatar";
	}
	
	@Override
	protected ICommandNode[] addCommands() {
		
		NodeBendingList bendingList = new NodeBendingList();
		NodeBendingAdd bendingAdd = new NodeBendingAdd();
		NodeBendingRemove bendingRemove = new NodeBendingRemove();
		NodeBranch branchBending = new NodeBranch(AvatarChatMessages.MSG_BENDING_BRANCH_INFO, "bending",
				bendingList, bendingAdd, bendingRemove);
		
		return new ICommandNode[] { branchBending };
		
	}
	
}
