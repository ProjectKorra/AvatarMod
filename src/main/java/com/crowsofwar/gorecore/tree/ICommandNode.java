package com.crowsofwar.gorecore.tree;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;

import net.minecraft.command.ICommandSender;

public interface ICommandNode {
	
	ICommandNode execute(CommandCall call, List<String> options);
	
	boolean needsOpPermission();
	
	String getNodeName();
	
	IArgument<?>[] getArgumentList();
	
	String getHelp();
	
	ChatMessage getInfoMessage();
	
	/**
	 * Get a list of tab completion suggestions while the player is typing an argument. Please don't
	 * return null.
	 * 
	 * @param sender
	 *            Player who is typing the message
	 * @param currentInput
	 *            What is typed so far for the argument
	 * @param argument
	 *            The argument the player is typing for
	 */
	List<String> getCompletionSuggestions(ICommandSender sender, String currentInput, IArgument<?> argument);
	
}
