package com.crowsofwar.gorecore.tree;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ArgumentRangeInteger implements IArgument<Integer> {
	
	private final int defaultValue;
	private final boolean optional;
	private final int min;
	private final int max;
	private final String name;
	
	public ArgumentRangeInteger(String name, int min, int max) {
		this.name = name;
		this.defaultValue = 0;
		this.optional = false;
		this.min = min;
		this.max = max;
	}
	
	public ArgumentRangeInteger(String name, int min, int max, int defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.optional = true;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	@Override
	public Integer getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public Integer convert(String input) {
		int value = ITypeConverter.CONVERTER_INTEGER.convert(input);
		if (value < min || value > max) {
			throw new TreeCommandException("gc.tree.error.rangeInt", name, min, max);
		}
		return value;
	}
	
	@Override
	public String getArgumentName() {
		return name;
	}
	
	@Override
	public String getHelpString() {
		char open = isOptional() ? '[' : '<';
		char close = isOptional() ? ']' : '>';
		return open + "any number " + min + "-" + max + close;
	}
	
	@Override
	public String getSpecificationString() {
		char open = isOptional() ? '[' : '<';
		char close = isOptional() ? ']' : '>';
		return open + name + close;
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		return new ArrayList<>();
	}
	
}
