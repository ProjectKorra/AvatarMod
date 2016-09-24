package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarConfig;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeConfig extends NodeFunctional {
	
	public NodeConfig() {
		super("config", true);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		AvatarConfig.load();
		return null;
	}
	
}
