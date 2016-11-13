package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class NodeProgressPoints extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	
	/**
	 * @param name
	 */
	public NodeProgressPoints(String name) {
		super(name, true);
		this.argPlayerName = new ArgumentPlayerName("player");
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList argList = call.popArguments(this);
		String playerName = argList.get(argPlayerName);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(call.getFrom().getEntityWorld(),
				playerName);
		if (data != null) {
			execute(data, playerName);
		}
		
		return null;
	}
	
	protected abstract void execute(AvatarPlayerData data, String playerName) {
		
	}
	
	public static class Add extends NodeProgressPoints {
		
		public Add() {
			super("add");
		}
		
	}
	
}
