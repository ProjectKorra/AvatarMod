package com.crowsofwar.avatar.common.command;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.gorecore.tree.IArgument;

import net.minecraft.command.ICommandSender;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ArgumentBendingController implements IArgument<BendingController> {
	
	private final String name;
	
	public ArgumentBendingController(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isOptional() {
		return false;
	}
	
	@Override
	public BendingController getDefaultValue() {
		return null;
	}
	
	@Override
	public BendingController convert(String input) {
		return BendingManager.getBending(input.toLowerCase());
	}
	
	@Override
	public String getArgumentName() {
		return name;
	}
	
	@Override
	public String getHelpString() {
		String out = "<";
		for (BendingController bc : BendingManager.allBending()) {
			out += bc.getControllerName() + "|";
		}
		return out.substring(0, out.length() - 1) + ">";
	}
	
	@Override
	public String getSpecificationString() {
		return "<" + name + ">";
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		List<String> out = new ArrayList<>();
		for (BendingController bc : BendingManager.allBending())
			out.add(bc.getControllerName());
		return out;
	}
	
}
