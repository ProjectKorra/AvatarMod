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

package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.tree.ArgumentDirect;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestNode1 extends NodeFunctional {
	
	private final IArgument<String> argA;
	private final IArgument<Integer> argB;
	
	public TestNode1() {
		super("node1", false);
		argA = new ArgumentDirect<String>("item", ITypeConverter.CONVERTER_STRING);
		argB = new ArgumentDirect<Integer>("amount", ITypeConverter.CONVERTER_INTEGER);
		addArguments(argA, argB);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(argA, argB);
		String a = args.get(argA);
		Integer b = args.get(argB);
		call.getFrom().addChatMessage(new TextComponentTranslation(b + " " + a + "s"));
		
		return null;
	}
	
}
