package com.crowsofwar.gorecore.tree;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;

/**
 * A node in the Tree-Command model.
 * <p>
 * Each node is responsible for executing the function, and optionally passing along the chain of
 * tree nodes. It also has many other things such as argument lists and help suggestions.
 * 
 * @author CrowsOfWar
 */
public interface ICommandNode {
	
	ICommandNode execute(CommandCall call, List<String> options);
	
	boolean needsOpPermission();
	
	String getNodeName();
	
	IArgument<?>[] getArgumentList();
	
	String getHelp();
	
	ChatMessage getInfoMessage();
	
}
