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
import java.util.function.Consumer;

/**
 * Allows quick creation of a NodeFunctional at runtime via the builder pattern
 * and lambdas.
 * 
 * @author CrowsOfWar
 */
public class NodeBuilder {
	
	private final String name;
	private boolean op;
	private final List<IArgument> args;
	
	public NodeBuilder(String name) {
		this.name = name;
		this.args = new ArrayList<>();
	}
	
	public NodeBuilder addArgument(IArgument argument) {
		this.args.add(argument);
		return this;
	}
	
	public NodeBuilder addArgumentDirect(String argumentName, ITypeConverter<?> converter) {
		return addArgument(new ArgumentDirect<>(argumentName, converter));
	}
	
	public NodeBuilder addArgumentPlayer(String argumentName) {
		return addArgument(new ArgumentPlayerName(argumentName));
	}
	
	public NodeFunctional build(Consumer<ArgumentList> action) {
		NodeFunctional node = new NodeFunctional(name, op) {
			@Override
			protected ICommandNode doFunction(CommandCall call, List<String> options) {
				ArgumentList argList = call.popArguments(this);
				action.accept(argList);
				return null;
			}
		};
		for (IArgument arg : args)
			node.addArgument(arg);
		
		return node;
	}
	
	public static class ArgPopper {
		
		private ArgumentList values;
		private List<IArgument> args;
		private int nextArg;
		
		public ArgPopper(ArgumentList values, List<IArgument> args) {
			this.values = values;
			this.args = args;
			this.nextArg = 0;
		}
		
		//@formatter:off
		/**
		 * Get the next argument from this popper. Arguments must be popped in
		 * the same order they were declared.
		 * <p>
		 * Example:
		 * 
		 * <pre>
		 * new NodeBuilder("sendMsg")
		 * 		.addArgumentPlayer("player")
		 * 		.addArgumentDirect("msg", ITypeConverter.CONVERTER_STRING)
		 * 		.build(popper -> {
		 * 			// Must use exact same order as used in addArgument methods
		 * 			String playerName = popper.get();
		 * 			String message = popper.get();
		 * 		});
		 * </pre>
		 */
		//@formatter:on
		public <T> T get() {
			if (nextArg >= args.size()) throw new IndexOutOfBoundsException("Ran out of arguments to pop");
			IArgument<T> key = args.get(nextArg++);
			return values.get(key);
		}
		
	}
	
}
