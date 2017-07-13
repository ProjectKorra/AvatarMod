/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.tree;

import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.format.MessageConfiguration;
import com.crowsofwar.gorecore.format.MultiMessage;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.gorecore.format.FormattedMessage.newChatMessage;

public abstract class TreeCommand implements ICommand {
	
	private NodeBranch branchRoot;
	protected final List<String> aliases;
	
	public TreeCommand() {
		// TODO Think of a default theme that doesn't look like neon barf.
		this(new MessageConfiguration().addColor("value", TextFormatting.GREEN).addColor("title",
				TextFormatting.BLUE));
	}
	
	public TreeCommand(MessageConfiguration cfg) {
		initChatMessages(cfg);
		branchRoot = new NodeBranch(newChatMessage(cfg, "gc.tree.branchHelp.root", "command"),
				getName(), addCommands());
		this.aliases = new ArrayList<>();
	}
	
	@Override
	public int compareTo(ICommand cmd) {
		return 0;
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return branchRoot.getHelp();
	}
	
	@Override
	public List getAliases() {
		return aliases;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments)
			throws CommandException {
		
		try {
			
			String allOptions = arguments.length > 0 ? arguments[arguments.length - 1] : "";
			boolean hasOptions = allOptions.startsWith("--");
			List<String> options = Arrays.asList(new String[0]);
			if (hasOptions) {
				options = Arrays.asList(allOptions.substring(2).split(","));
				arguments = Arrays.copyOfRange(arguments, 0, arguments.length - 1);
			}
			
			CommandCall call = new CommandCall(sender, arguments);
			
			String path = "/" + getName();
			
			ICommandNode node = branchRoot;
			while (node != null) {
				
				if (node.needsOpPermission() && !call.isOpped())
					throw new TreeCommandException(Reason.NO_PERMISSION);
				
				if (call.getArgumentsLeft() == 0 && options.contains("help")) {
					
					if (node instanceof NodeBranch) {
						if (node == branchRoot) {
							sendCommandHelp(sender);
						} else {
							sendBranchHelp(sender, (NodeBranch) node, path);
						}
					} else {
						sendNodeHelp(sender, node);
					}
					
					node = null;
				} else {
					node = node.execute(call, options);
				}
				
				if (node != null) path += " " + node.getNodeName();
				
			}
			
		} catch (TreeCommandException e) {
			
			sender.sendMessage(new TextComponentTranslation(e.getMessage(), e.getFormattingArgs()));
		}
		
	}
	
	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int index) {
		return false;
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] sentArgs, BlockPos pos) {
		
		List<String> emptyList = Arrays.asList();
		
		// Basically, traverse the tree, going up the correct branches
		// to find the functional node. Then, call getCompletionSuggestions
		// on that node.
		
		CommandCall call = new CommandCall(sender, sentArgs);
		ICommandNode node = branchRoot;
		int nodeIndex = 0;
		while (node != null) {
			
			// If it is a branch, keep traversing the tree
			// Make sure that this isn't the last node (arguments left)
			if (node instanceof NodeBranch && call.getArgumentsLeft() > 1) {
				node = node.execute(call, emptyList);
				nodeIndex++;
			} else {
				IArgument<?>[] nodeArgs = node.getArgumentList();
				IArgument<?> useArg = nodeArgs[sentArgs.length - 1 - nodeIndex];
				List<String> suggestions = nodeArgs[sentArgs.length - 1 - nodeIndex]
						.getCompletionSuggestions(sender, sentArgs[sentArgs.length - 1].toLowerCase());
				
				String lastArg = sentArgs[sentArgs.length - 1];
				List<String> ret = suggestions.stream().filter(suggestion -> suggestion.startsWith(lastArg))
						.collect(Collectors.toList());
				return ret;
				
			}
			
		}
		
		return emptyList;
		
	}
	
	private void sendCommandHelp(ICommandSender sender) {
		cmdHelpTop.send(sender, getName());
		cmdHelpCommandOverview.send(sender);
		
		MultiMessage multi = cmdHelpNodes.chain();
		
		ICommandNode[] allNodes = branchRoot.getSubNodes();
		for (int i = 0; i < allNodes.length; i++) {
			multi.add(cmdHelpNodeItem, allNodes[i].getNodeName());
			if (i < allNodes.length - 1) {
				FormattedMessage separator = cmdHelpSeparator;
				if (i == allNodes.length - 2) separator = cmdHelpSeparatorLast;
				multi.add(separator);
			}
		}
		multi.send(sender);
		
	}
	
	private void sendBranchHelp(ICommandSender sender, NodeBranch branch, String path) {
		
		branchHelpTop.send(sender, branch.getNodeName());
		branchHelpNotice.send(sender);
		branchHelpInfo.chain().add(branch.getInfoMessage()).send(sender);
		
		MultiMessage chain = branchHelpOptions.chain();
		ICommandNode[] subNodes = branch.getSubNodes();
		for (int i = 0; i < subNodes.length; i++) {
			chain.add(branchHelpOptionsItem, subNodes[i].getNodeName());
			if (i < subNodes.length - 1) chain.add(
					i == subNodes.length - 2 ? branchHelpOptionsSeparatorLast : branchHelpOptionsSeparator);
			
		}
		chain.send(sender);
		
		branchHelpExample.send(sender, path, subNodes[0].getNodeName());
		
	}
	
	private void sendNodeHelp(ICommandSender sender, ICommandNode node) {
		
		nodeHelpTop.send(sender, node.getNodeName());
		nodeHelpDesc.chain().add(node.getInfoMessage()).send(sender);
		
		if (node.getArgumentList().length == 0) {
			
			nodeHelpArgsNone.send(sender);
			
		} else {
			
			MultiMessage msgArguments = nodeHelpArgs.chain();
			for (IArgument<?> arg : node.getArgumentList())
				msgArguments.add(nodeHelpArgsItem, arg.getArgumentName());
			msgArguments.send(sender);
			
			MultiMessage msgAccepted = nodeHelpAccepted.chain();
			for (IArgument<?> arg : node.getArgumentList())
				msgAccepted.add(nodeHelpAcceptedItem, arg.getHelpString());
			msgAccepted.send(sender);
			
		}
		
	}
	
	/**
	 * Called to instantiate all subclass Command Nodes. Return the ones that
	 * should be added to the root branch.
	 */
	protected abstract ICommandNode[] addCommands();
	
	protected FormattedMessage cmdHelpTop;
	protected FormattedMessage cmdHelpNodes;
	protected FormattedMessage cmdHelpNodeItem;
	protected FormattedMessage cmdHelpSeparator;
	protected FormattedMessage cmdHelpSeparatorLast;
	protected FormattedMessage cmdHelpCommandOverview;
	
	protected FormattedMessage branchHelpTop;
	protected FormattedMessage branchHelpNotice;
	protected FormattedMessage branchHelpInfo;
	protected FormattedMessage branchHelpOptions;
	protected FormattedMessage branchHelpOptionsItem;
	protected FormattedMessage branchHelpOptionsSeparator;
	protected FormattedMessage branchHelpOptionsSeparatorLast;
	protected FormattedMessage branchHelpExample;
	protected FormattedMessage branchHelpDefault;
	
	protected FormattedMessage nodeHelpTop;
	protected FormattedMessage nodeHelpDesc;
	protected FormattedMessage nodeHelpArgs;
	protected FormattedMessage nodeHelpArgsItem;
	protected FormattedMessage nodeHelpArgsNone;
	protected FormattedMessage nodeHelpAccepted;
	protected FormattedMessage nodeHelpAcceptedItem;
	
	private void initChatMessages(MessageConfiguration cfg) {
		
		cmdHelpTop = newChatMessage(cfg, "gc.tree.cmdhelp.top", "name");
		cmdHelpNodes = newChatMessage(cfg, "gc.tree.cmdhelp.nodes");
		cmdHelpNodeItem = newChatMessage(cfg, "gc.tree.cmdhelp.nodes.item", "node");
		cmdHelpSeparator = newChatMessage(cfg, "gc.tree.cmdhelp.nodes.separator");
		cmdHelpSeparatorLast = newChatMessage(cfg, "gc.tree.cmdhelp.nodes.separatorLast");
		cmdHelpCommandOverview = newChatMessage(cfg, "gc.tree.cmdhelp.showCmdInfo");
		
		branchHelpTop = newChatMessage(cfg, "gc.tree.branchHelp.top", "name");
		branchHelpNotice = newChatMessage(cfg, "gc.tree.branchHelp.notice");
		branchHelpInfo = newChatMessage(cfg, "gc.tree.branchHelp.info");
		branchHelpOptions = newChatMessage(cfg, "gc.tree.branchHelp.options");
		branchHelpOptionsItem = newChatMessage(cfg, "gc.tree.branchHelp.options.item", "node");
		branchHelpOptionsSeparator = newChatMessage(cfg, "gc.tree.branchHelp.options.separator");
		branchHelpOptionsSeparatorLast = newChatMessage(cfg, "gc.tree.branchHelp.options.separatorLast");
		branchHelpExample = newChatMessage(cfg, "gc.tree.branchHelp.example", "path", "node-name");
		branchHelpDefault = newChatMessage(cfg, "gc.tree.branch.defaultInfo");
		
		nodeHelpTop = newChatMessage(cfg, "gc.tree.nodeHelp.top", "name");
		nodeHelpDesc = newChatMessage(cfg, "gc.tree.nodeHelp.desc");
		nodeHelpArgs = newChatMessage(cfg, "gc.tree.nodeHelp.args");
		nodeHelpArgsItem = newChatMessage(cfg, "gc.tree.nodeHelp.args.item", "argument");
		nodeHelpArgsNone = newChatMessage(cfg, "gc.tree.nodeHelp.args.none");
		nodeHelpAccepted = newChatMessage(cfg, "gc.tree.nodeHelp.accepted");
		nodeHelpAcceptedItem = newChatMessage(cfg, "gc.tree.nodeHelp.accepted.item", "input");
		
	}
	
}
