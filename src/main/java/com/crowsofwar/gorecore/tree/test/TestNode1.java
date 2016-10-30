package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestNode1 extends NodeFunctional {
	
	private final IArgument<String> argA;
	private final IArgument<Integer> argB;
	
	public TestNode1() {
		super("node1", false);
		argA = addArgument(new ArgumentDirect<String>("item", ITypeConverter.CONVERTER_STRING));
		argB = addArgument(new ArgumentDirect<Integer>("amount", ITypeConverter.CONVERTER_INTEGER));
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(this);
		String a = args.get(argA);
		Integer b = args.get(argB);
		call.getFrom().addChatMessage(new TextComponentTranslation(b + " " + a + "s"));
		
		return null;
	}
	
}
