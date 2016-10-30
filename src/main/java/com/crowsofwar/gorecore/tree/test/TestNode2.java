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

public class TestNode2 extends NodeFunctional {
	
	private final IArgument<Double> argTemp;
	
	public TestNode2() {
		super("node2", false);
		argTemp = new ArgumentDirect("temperature", ITypeConverter.CONVERTER_DOUBLE, 3);
		addArgument(argTemp);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(this);
		call.getFrom()
				.addChatMessage(new TextComponentTranslation("The temperature is " + args.get(argTemp)));
		return null;
	}
	
}
