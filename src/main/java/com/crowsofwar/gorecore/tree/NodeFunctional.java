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
import java.util.Arrays;
import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;

import net.minecraft.util.text.TextComponentTranslation;

/**
 * A very customizable implementation of {@link ICommandNode}. This is designed
 * to simplify the development of new command nodes by implementing many of
 * ICommandNode's methods.
 * 
 * @author CrowsOfWar
 */
public abstract class NodeFunctional implements ICommandNode {
	
	private static final ChatMessage DEFAULT_INFO;
	static {
		DEFAULT_INFO = ChatMessage.newChatMessage("gc.tree.node.defaultInfo");
	}
	
	private final String name;
	private final boolean op;
	private List<IArgument> args;
	
	public NodeFunctional(String name, boolean op) {
		this.name = name;
		this.op = op;
		this.args = new ArrayList<>();
	}
	
	protected <T extends IArgument<?>> T addArgument(T argument) {
		this.args.add(argument);
		return argument;
	}
	
	protected void addArguments(IArgument<?>... arguments) {
		this.args.addAll(Arrays.asList(arguments));
	}
	
	@Override
	public final boolean needsOpPermission() {
		return op;
	}
	
	@Override
	public final String getNodeName() {
		return name;
	}
	
	@Override
	public final IArgument<?>[] getArgumentList() {
		IArgument<?>[] arr = new IArgument[args.size()];
		return args.toArray(arr);
	}
	
	@Override
	public String getHelp() {
		String out = getNodeName();
		IArgument<?>[] args = getArgumentList();
		for (int i = 0; i < args.length; i++) {
			out += " " + args[i].getSpecificationString();
		}
		return out;
	}
	
	@Override
	public final ICommandNode execute(CommandCall call, List<String> options) {
		if (options.contains("help")) {
			call.getFrom()
					.addChatMessage(new TextComponentTranslation("gc.tree.help", getHelp(), getNodeName()));
			return null;
		} else {
			return doFunction(call, options);
		}
	}
	
	protected abstract ICommandNode doFunction(CommandCall call, List<String> options);
	
	@Override
	public ChatMessage getInfoMessage() {
		return DEFAULT_INFO;
	}
	
}
