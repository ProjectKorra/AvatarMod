/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.tree;

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

public class ArgumentList {
	
	private final Map<IArgument<?>, Object> argumentValues;
	
	public ArgumentList(String[] userInput, IArgument<?>[] arguments) {
		
		argumentValues = new HashMap<>();
		for (int i = 0; i < arguments.length; i++) {
			IArgument<?> argument = arguments[i];
			Object out = null;
			if (i < userInput.length) { // If possible, prefer user input over
										// default
				out = argument.convert(userInput[i]);
			} else { // Try to use the default value if the argument is optional
				if (argument.isOptional()) { // Argument has a default value,
												// which can be used
					out = argument.getDefaultValue();
				} else { // Argument isn't optional, but user input hasn't been
							// specified. Throw an
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
