package com.crowsofwar.avatar.common.command;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeBranch;
import com.crowsofwar.gorecore.tree.TreeCommand;

public class AvatarCommand extends TreeCommand {
	
	public static final ITypeConverter<BendingController> CONVERTER_BENDING = new ITypeConverter<BendingController>() {
		
		@Override
		public BendingController convert(String str) {
			return BendingManager.getBending(str);
		}
		
		@Override
		public String toString(BendingController obj) {
			return obj.getControllerName();
		}
		
		@Override
		public String getTypeName() {
			return "Bending";
		}
		
	};
	
	public AvatarCommand() {
		super(AvatarChatMessages.CFG);
	}
	
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
