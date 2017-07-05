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

import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestCakeFrost extends NodeFunctional {
	
	public TestCakeFrost() {
		super("frost", true);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		String end = options.contains("fancy") ? ".fancy" : "";
		call.getFrom().addChatMessage(new TextComponentTranslation("test.frostCake" + end));
		return null;
	}
	
	@Override
	public FormattedMessage getInfoMessage() {
		return TestMessages.MSG_CAKE_FROST_HELP;
	}
	
}
