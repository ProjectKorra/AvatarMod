package com.crowsofwar.gorecore.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

import net.minecraft.command.ICommandSender;

public class NodeBranch implements ICommandNode {
	
	private final ICommandNode[] nodes;
	private final IArgument<String> argName;
	private final IArgument<?>[] args;
	private final String name;
	private final ChatMessage infoMessage;
	
	public NodeBranch(ChatMessage infoMessage, String name, ICommandNode... nodes) {
		this.nodes = nodes;
		// this.argName = new ArgumentDirect<String>("node-name", ITypeConverter.CONVERTER_STRING);
		String[] possibilities = new String[nodes.length];
		for (int i = 0; i < possibilities.length; i++)
			possibilities[i] = nodes[i].getNodeName();
		this.argName = new ArgumentOptions<String>(ITypeConverter.CONVERTER_STRING, "node-name",
				possibilities);
		this.args = new IArgument<?>[] { argName };
		this.name = name;
		this.infoMessage = infoMessage;
	}
	
	@Override
	public ICommandNode execute(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(argName);
		String name = args.get(argName);
		for (int i = 0; i < nodes.length; i++) {
			System.out.println(nodes[i].getNodeName() + "/" + name);
			if (nodes[i].getNodeName().equals(name)) return nodes[i];
		}
		throw new TreeCommandException(Reason.NO_BRANCH_NODE, name, getHelp());
	}
	
	@Override
	public boolean needsOpPermission() {
		return false;
	}
	
	@Override
	public String getNodeName() {
		return name;
	}
	
	@Override
	public IArgument<?>[] getArgumentList() {
		return args;
	}
	
	@Override
	public String getHelp() {
		return getNodeName() + " " + argName.getHelpString();
	}
	
	public ICommandNode[] getSubNodes() {
		return nodes;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return infoMessage;
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput,
			IArgument<?> argument) {
		
		List<String> out = new ArrayList<>();
		Arrays.asList(nodes).forEach(node -> out.add(node.getNodeName()));
		
		out.sort((String str1, String str2) -> {
			// See if any string starts with current input
			if (!currentInput.isEmpty() && str1.startsWith(currentInput)) return -1;
			if (!currentInput.isEmpty() && str2.startsWith(currentInput)) return 1;
			// Otherwise, just sort alphabetically
			return str1.compareTo(str2);
		});
		
		// Make sure that there are tab completions for what user has typed so far
		// If not, don't give any suggestions
		if (!out.get(0).startsWith(currentInput)) {
			return new ArrayList<>();
		}
		
		return out;
	}
	
}
