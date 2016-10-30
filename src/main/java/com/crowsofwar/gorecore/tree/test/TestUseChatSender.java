package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

public class TestUseChatSender extends NodeFunctional {
	
	private final IArgument<String> argFruit;
	
	public TestUseChatSender() {
		super("chatsender", false);
		this.argFruit = new ArgumentOptions<String>(ITypeConverter.CONVERTER_STRING, "fruit", "pineapple",
				"banana", "strawberry");
		addArgument(argFruit);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(this);
		String fruit = args.get(argFruit);
		TestMessages.MSG_FRUIT.send(call.getFrom(), fruit);
		return null;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return TestMessages.MSG_CHATSENDER_HELP;
	}
	
}
