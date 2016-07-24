package com.crowsofwar.avatar.common.command;

import java.util.List;

import crowsofwar.gorecore.tree.CommandCall;
import crowsofwar.gorecore.tree.ICommandNode;
import crowsofwar.gorecore.tree.NodeFunctional;

public class NodeBendingAdd extends NodeFunctional {
	
	public NodeBendingAdd() {
		super("add", true);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
