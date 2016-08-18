package com.crowsofwar.gorecore.tree;

public interface IArgument<T> {
	
	boolean isOptional();
	
	T getDefaultValue();
	
	T convert(String input);
	
	String getArgumentName();
	
	/**
	 * Format the argument into a user-friendly help string. e.g. &lt;ON|OFF>
	 */
	String getHelpString();
	
	/**
	 * Let the user know that the argument is there. e.g. [duration]
	 */
	String getSpecificationString();
	
}
