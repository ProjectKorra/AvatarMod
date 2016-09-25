package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarConfig;
import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeConfig extends NodeFunctional {
	
	private final IArgument<String> argKey;
	private final IArgument<String> argVal;
	
	public NodeConfig() {
		super("config", true);
		this.argKey = new ArgumentDirect<>("key", ITypeConverter.CONVERTER_STRING, "");
		this.argVal = new ArgumentDirect<>("value", ITypeConverter.CONVERTER_STRING, "");
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList list = call.popArguments(argKey, argVal);
		String key = list.get(argKey);
		String val = list.get(argVal);
		if (key.equals("") || val.equals("")) {
			AvatarConfig.load();
		} else {
			System.out.println("Load config " + key + "->" + val);
			
		}
		
		return null;
	}
	
}
