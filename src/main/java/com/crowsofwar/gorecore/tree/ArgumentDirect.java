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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;

/**
 * An argument that takes direct user input and converts it into a value. This allows all possible
 * values. Does not support tab completion.
 * 
 * @param <T>
 *            The type of value
 * 
 * @author CrowsOfWar
 */
public class ArgumentDirect<T> implements IArgument<T> {
	
	private final T defaultValue;
	private final ITypeConverter<T> converter;
	private final String name;
	
	public ArgumentDirect(String argumentName, ITypeConverter<T> converter) {
		this(argumentName, converter, null);
	}
	
	public ArgumentDirect(String argumentName, ITypeConverter<T> converter, T defaultValue) {
		this.defaultValue = defaultValue;
		this.converter = converter;
		this.name = argumentName;
	}
	
	@Override
	public boolean isOptional() {
		return defaultValue != null;
	}
	
	@Override
	public T getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public T convert(String input) {
		return converter.convert(input);
	}
	
	@Override
	public String getArgumentName() {
		return name;
	}
	
	@Override
	public String getHelpString() {
		String before = isOptional() ? "[" : "<";
		String after = isOptional() ? "]" : ">";
		return before + "any " + converter.getTypeName() + after;
	}
	
	@Override
	public String getSpecificationString() {
		String before = isOptional() ? "[" : "<";
		String after = isOptional() ? "]" : ">";
		return before + getArgumentName() + after;
	}
	
	@Override
	public List<String> getCompletionSuggestions(ICommandSender sender, String currentInput) {
		return new ArrayList<>();
	}
	
}
