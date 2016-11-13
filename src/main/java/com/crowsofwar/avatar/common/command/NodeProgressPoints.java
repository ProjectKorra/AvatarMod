package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.command.ICommandSender;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class NodeProgressPoints extends NodeFunctional {
	
	private final IArgument<String> argPlayerName;
	private final IArgument<BendingController> argBending;
	
	/**
	 * @param name
	 */
	public NodeProgressPoints(String name) {
		super(name, true);
		this.argPlayerName = new ArgumentPlayerName("player");
		this.argBending = new ArgumentBendingController("bending");
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList argList = call.popArguments(this);
		String playerName = argList.get(argPlayerName);
		BendingController bending = argList.get(argBending);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(call.getFrom().getEntityWorld(),
				playerName);
		if (data != null) {
			BendingState bs = data.getBendingState(bending.getType());
			execute(data, call.getFrom(), bs);
		}
		
		return null;
	}
	
	protected abstract void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending);
	
	public static class Add extends NodeProgressPoints {
		
		public Add() {
			super("add");
		}
		
		@Override
		protected void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending) {
			bending.addProgressPoint();
		}
		
	}
	
}
