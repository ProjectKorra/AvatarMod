package com.crowsofwar.gorecore.tree;

import java.util.List;

import net.minecraft.command.ICommandSender;

public interface IArgument<T> {
	
	boolean isOptional();
	
	T getDefaultValue();
	
	T convert(String input);
	
	String getArgumentName();
	
	/**
	 * Format the argument to show the accepted values of the argument. e.g. &lt;ON|OFF>
	 */
	String getHelpString();
	
	/**
	 * Show a description of what this argument represents. e.g. [duration]
	 */
	String getSpecificationString();
	
	/**
	 * Gets a list of tab completion suggestions while the player is typing an argument. The first
	 * item on the list is the item which will be auto-completed.
	 * <p>
	 * If there are no suggestions, returns an empty list (doesn't returns null).
	 * 
	 * @param sender
	 *            Player who is typing the message
	 * @param currentInput
	 *            What is typed so far for the argument
	 */
	List<String> getCompletionSuggestions(ICommandSender sender, String currentInput);
	
}
