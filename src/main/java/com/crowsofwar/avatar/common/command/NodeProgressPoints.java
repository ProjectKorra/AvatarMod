package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
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
		addArgument(this.argPlayerName = new ArgumentPlayerName("player"));
		addArgument(this.argBending = new ArgumentBendingController("bending"));
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
			execute(data, call.getFrom(), bs, playerName, argList);
		}
		
		return null;
	}
	
	protected abstract void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending,
			String player, ArgumentList args);
	
	public static class Add extends NodeProgressPoints {
		
		public Add() {
			super("add");
		}
		
		@Override
		protected void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending,
				String player, ArgumentList args) {
			bending.addProgressPoint();
			AvatarChatMessages.MSG_PROGRESS_POINT_ADDED.send(sender, player, bending.getProgressPoints());
		}
		
	}
	
	public static class Get extends NodeProgressPoints {
		
		public Get() {
			super("get");
		}
		
		@Override
		protected void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending,
				String player, ArgumentList args) {
			
			AvatarChatMessages.MSG_PROGRESS_POINT_GET.send(sender, player, bending.getProgressPoints());
			
		}
		
	}
	
	public static class Set extends NodeProgressPoints {
		
		private final IArgument<Integer> argAmount;
		
		/**
		 * @param name
		 */
		public Set() {
			super("set");
			addArgument(this.argAmount = new ArgumentDirect("amount", ITypeConverter.CONVERTER_INTEGER));
		}
		
		@Override
		protected void execute(AvatarPlayerData data, ICommandSender sender, BendingState bending,
				String player, ArgumentList args) {
			
			int amount = args.get(argAmount);
			if (amount > 0) {
				bending.setProgressPoints(amount);
				AvatarChatMessages.MSG_PROGRESS_POINT_SET.send(sender, player, amount);
			} else {
				AvatarChatMessages.MSG_PROGRESS_POINT_SET_RANGE.send(sender);
			}
			
		}
		
	}
	
}
