package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestBuyVideogames extends NodeFunctional {
	
	private final IArgument<Integer> argAmount;
	
	public TestBuyVideogames() {
		super("buy", true);
		addArguments(argAmount = new ArgumentDirect<Integer>("amount", ITypeConverter.CONVERTER_INTEGER, 1));
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(argAmount);
		int amount = args.get(argAmount);
		call.getFrom().addChatMessage(new TextComponentTranslation("test.buyVideogames", amount));
		return null;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return TestMessages.MSG_VIDEOGAME_HELP;
	}
	
}
