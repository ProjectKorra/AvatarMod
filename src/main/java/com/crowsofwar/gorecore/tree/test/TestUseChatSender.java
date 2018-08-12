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

import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.tree.*;

import java.util.List;

public class TestUseChatSender extends NodeFunctional {

	private final IArgument<String> argFruit;

	public TestUseChatSender() {
		super("chatsender", false);
		argFruit = new ArgumentOptions<>(ITypeConverter.CONVERTER_STRING, "fruit", "pineapple", "banana", "strawberry");
		addArgument(argFruit);
	}

	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		ArgumentList args = call.popArguments(this);
		String fruit = args.get(argFruit);
		TestMessages.MSG_FRUIT.send(call.getFrom(), fruit);
		return null;
	}

	@Override
	public FormattedMessage getInfoMessage() {
		return TestMessages.MSG_CHATSENDER_HELP;
	}

}
