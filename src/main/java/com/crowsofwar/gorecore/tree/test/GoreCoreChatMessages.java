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

import static com.crowsofwar.gorecore.chat.ChatMessage.newChatMessage;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.chat.MessageConfiguration;

import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GoreCoreChatMessages {
	
	public static void register() {}
	
	public static final MessageConfiguration CFG = new MessageConfiguration().addColor("value",
			TextFormatting.GOLD);
	public static final ChatMessage MSG_FIXID_SUCCESS = newChatMessage(CFG, "gc.cmd.fixid.success", "player");
	public static final ChatMessage MSG_FIXID_FAILURE = newChatMessage(CFG, "gc.cmd.fixid.failure", "player");
	public static final ChatMessage MSG_FIXID_ONLINE = newChatMessage(CFG, "gc.cmd.fixid.online", "player");
	public static final ChatMessage MSG_FIXID_CONFIRM = newChatMessage(CFG, "gc.cmd.fixid.confirm", "player");
	public static final ChatMessage MSG_FIXID_CONFIRM2 = newChatMessage(CFG, "gc.cmd.fixid.confirm2",
			"player");
	
}
