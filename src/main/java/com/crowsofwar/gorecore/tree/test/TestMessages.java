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

import static com.crowsofwar.gorecore.chat.ChatSender.newChatMessage;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.chat.ChatSender;
import com.crowsofwar.gorecore.chat.MessageConfiguration;

import net.minecraft.util.text.TextFormatting;

public class TestMessages {
	
	// @formatter:off
	public static final MessageConfiguration CFG = new MessageConfiguration()
			.addColor("special", TextFormatting.LIGHT_PURPLE)
			.addConstant("const", "This_is_a_constant");
	public static final ChatMessage MSG_VIDEOGAME_HELP = newChatMessage(CFG, "test.buyVideogames.help");
	public static final ChatMessage MSG_CAKE_FROST_HELP = newChatMessage(CFG, "test.frostCake.help");
	public static final ChatMessage MSG_CAKE_LICK_HELP = newChatMessage(CFG, "test.lickCake.help");
	public static final ChatMessage MSG_PLAYVIDEOGAMES_HELP = newChatMessage(CFG, "test.videogames.help");
	public static final ChatMessage MSG_CHATSENDER_HELP = newChatMessage(CFG, "test.chatSender.help");
	public static final ChatMessage MSG_VIDEOGAME_BRANCH_HELP = newChatMessage(CFG, "test.videogamesBranch.help");
	public static final ChatMessage MSG_FRUIT = ChatSender.newChatMessage(CFG, "test.chatSender", "fruit");
	public static final ChatMessage MSG_CONST = ChatSender.newChatMessage(CFG, "test.const");
	
}
