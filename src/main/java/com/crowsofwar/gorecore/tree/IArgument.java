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

import java.util.List;

import net.minecraft.command.ICommandSender;

/**
 * Represents an argument for a command node.
 * <p>
 * Is responsible for converting the values of the argument to/from Strings.
 * 
 * @param <T>
 *            Type of the argument
 * 
 * @author CrowsOfWar
 */
public interface IArgument<T> {
	
	/**
	 * Returns whether this argument can be omitted for the command node to
	 * still work.
	 */
	boolean isOptional();
	
	/**
	 * Gets the default value of this argument. Null if not optional.
	 */
	T getDefaultValue();
	
	/**
	 * Return a value based off of the given input.
	 * 
	 * @param input
	 *            Input string
	 */
	T convert(String input);
	
	/**
	 * Get the name of this argument to show up in the help pages
	 */
	String getArgumentName();
	
	/**
	 * Format the argument to show the accepted values of the argument. e.g.
	 * &lt;ON|OFF>
	 */
	String getHelpString();
	
	/**
	 * Show a description of what this argument represents. e.g. [duration]
	 */
	String getSpecificationString();
	
	/**
	 * Gets a list of tab completion suggestions while the player is typing an
	 * argument. The first item on the list is the item which will be
	 * auto-completed.
	 * <p>
	 * If there are no suggestions, returns an empty list (doesn't returns
	 * null).
	 * 
	 * @param sender
	 *            Player who is typing the message
	 * @param currentInput
	 *            What is typed so far for the argument
	 */
	List<String> getCompletionSuggestions(ICommandSender sender, String currentInput);
	
}
