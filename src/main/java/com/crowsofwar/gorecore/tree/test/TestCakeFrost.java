package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestCakeFrost extends NodeFunctional {
	
	public TestCakeFrost() {
		super("frost", true);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		String end = options.contains("fancy") ? ".fancy" : "";
		call.getFrom().addChatMessage(new TextComponentTranslation("test.frostCake" + end));
		return null;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return TestMessages.MSG_CAKE_FROST_HELP;
	}
	
}
