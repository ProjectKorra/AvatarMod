package com.crowsofwar.gorecore.tree;

import static com.crowsofwar.gorecore.chat.ChatSender.newChatMessage;

import java.util.Arrays;
import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.chat.MessageConfiguration;
import com.crowsofwar.gorecore.chat.MultiMessage;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public abstract class TreeCommand implements ICommand {
	
	private NodeBranch branchRoot;
	
	public TreeCommand() {
		this(new MessageConfiguration().addColor("value", EnumChatFormatting.GREEN).addColor("title", EnumChatFormatting.LIGHT_PURPLE));
	}
	
	public TreeCommand(MessageConfiguration cfg) {
		initChatMessages(cfg);
		branchRoot = new NodeBranch(newChatMessage(cfg, "gc.tree.branchHelp.root", "command"), getCommandName(), addCommands());
	}
	
	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return branchRoot.getHelp();
	}
	
	@Override
	public List getCommandAliases() {
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {
		
		try {
			
			String allOptions = arguments.length > 0 ? arguments[arguments.length - 1] : "";
			boolean hasOptions = allOptions.startsWith("--");
			List<String> options = Arrays.asList(new String[0]);
			if (hasOptions) {
				options = Arrays.asList(allOptions.substring(2).split(","));
				arguments = Arrays.copyOfRange(arguments, 0, arguments.length - 1);
			}
			
			CommandCall call = new CommandCall(sender, arguments);
			
			String path = "/" + getCommandName();
			
			ICommandNode node = branchRoot;
			while (node != null) {
				
				if (node.needsOpPermission() && !call.isOpped()) throw new TreeCommandException(Reason.NO_PERMISSION);
				
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
			
			sender.addChatMessage(new ChatComponentTranslation(e.getMessage(), e.getFormattingArgs()));
		}
		
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return null;
	}
	
	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
	
	private void sendCommandHelp(ICommandSender sender) {
		cmdHelpTop.send(sender, getCommandName());
		cmdHelpCommandOverview.send(sender);
		
		MultiMessage multi = cmdHelpNodes.chain();
		
		ICommandNode[] allNodes = branchRoot.getSubNodes();
		for (int i = 0; i < allNodes.length; i++) {
			multi.add(cmdHelpNodeItem, allNodes[i].getNodeName());
			if (i < allNodes.length - 1) {
				ChatMessage separator = cmdHelpSeparator;
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
			if (i < subNodes.length - 1) chain.add(i == subNodes.length - 2 ? branchHelpOptionsSeparatorLast : branchHelpOptionsSeparator);
			
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
	 * Called to instantiate all subclass Command Nodes. Return the ones that should be added to the
	 * root branch.
	 */
	protected abstract ICommandNode[] addCommands();
	
	protected ChatMessage cmdHelpTop;
	protected ChatMessage cmdHelpNodes;
	protected ChatMessage cmdHelpNodeItem;
	protected ChatMessage cmdHelpSeparator;
	protected ChatMessage cmdHelpSeparatorLast;
	protected ChatMessage cmdHelpCommandOverview;
	
	protected ChatMessage branchHelpTop;
	protected ChatMessage branchHelpNotice;
	protected ChatMessage branchHelpInfo;
	protected ChatMessage branchHelpOptions;
	protected ChatMessage branchHelpOptionsItem;
	protected ChatMessage branchHelpOptionsSeparator;
	protected ChatMessage branchHelpOptionsSeparatorLast;
	protected ChatMessage branchHelpExample;
	protected ChatMessage branchHelpDefault;
	
	protected ChatMessage nodeHelpTop;
	protected ChatMessage nodeHelpDesc;
	protected ChatMessage nodeHelpArgs;
	protected ChatMessage nodeHelpArgsItem;
	protected ChatMessage nodeHelpArgsNone;
	protected ChatMessage nodeHelpAccepted;
	protected ChatMessage nodeHelpAcceptedItem;
	
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