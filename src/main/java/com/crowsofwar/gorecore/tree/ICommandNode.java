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

import com.crowsofwar.gorecore.chat.ChatMessage;

/**
 * A node in the Tree-Command model.
 * <p>
 * Each node is responsible for executing the function, and optionally passing along the chain of
 * tree nodes. It also has many other things such as argument lists and help suggestions.
 * 
 * @author CrowsOfWar
 */
public interface ICommandNode {
	
	ICommandNode execute(CommandCall call, List<String> options);
	
	boolean needsOpPermission();
	
	String getNodeName();
	
	IArgument<?>[] getArgumentList();
	
	String getHelp();
	
	ChatMessage getInfoMessage();
	
}
