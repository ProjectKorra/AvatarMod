package com.crowsofwar.gorecore.tree;

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

public class ArgumentList {
	
	private final Map<IArgument<?>, Object> argumentValues;
	
	public ArgumentList(String[] userInput, IArgument<?>[] arguments) {
		
		argumentValues = new HashMap<IArgument<?>, Object>();
		for (int i = 0; i < arguments.length; i++) {
			IArgument<?> argument = arguments[i];
			Object out = null;
			if (i < userInput.length) { // If possible, prefer user input over default
				out = argument.convert(userInput[i]);
			} else { // Try to use the default value if the argument is optional
				if (argument.isOptional()) { // Argument has a default value, which can be used
					out = argument.getDefaultValue();
				} else { // Argument isn't optional, but user input hasn't been specified. Throw an
							// error.
					throw new TreeCommandException(Reason.ARGUMENT_MISSING, arguments[i].getArgumentName());
				}
			}
			argumentValues.put(argument, out);
		}
		
	}
	
	public <T> T get(IArgument<T> argument) {
		return (T) argumentValues.get(argument);
	}
	
	public int length() {
		return argumentValues.size();
	}
	
}
