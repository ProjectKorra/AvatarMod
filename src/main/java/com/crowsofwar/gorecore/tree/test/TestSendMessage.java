package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class TestSendMessage extends NodeFunctional {
	
	private final ChatMessage message;
	
	/**
	 * @param name
	 * @param op
	 */
	public TestSendMessage(String name, ChatMessage message) {
		super(name, false);
		this.message = message;
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		message.send(call.getFrom());
		return null;
	}
	
}
