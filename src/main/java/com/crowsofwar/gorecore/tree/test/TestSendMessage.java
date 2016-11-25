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

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class TestSendMessage extends NodeFunctional {
	
	private final ChatMessage message;
	
	/**
	 * @param name
	 * @param op
	 */
	public TestSendMessage(String name, ChatMessage message) {
		super(name, false);
		this.message = message;
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		message.send(call.getFrom());
		return null;
	}
	
}
