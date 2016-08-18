package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

public class TestCakeLick extends NodeFunctional {
	
	private IArgument<Double> argGallons;
	
	public TestCakeLick() {
		super("lick", false);
		argGallons = new ArgumentDirect<Double>("gallons", ITypeConverter.CONVERTER_DOUBLE);
		addArguments(argGallons);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		double gallons = call.popArguments(argGallons).get(argGallons);
		call.getFrom().addChatMessage(new ChatComponentTranslation("test.lickCake", gallons));
		return null;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return TestMessages.MSG_CAKE_LICK_HELP;
	}
	
}
