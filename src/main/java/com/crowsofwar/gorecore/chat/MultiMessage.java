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

package com.crowsofwar.gorecore.chat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;

public class MultiMessage {
	
	private final List<ChatMessage> chatMessages;
	private final List<Object[]> formattingArgs;
	
	MultiMessage() {
		this.chatMessages = new ArrayList<ChatMessage>();
		this.formattingArgs = new ArrayList<Object[]>();
	}
	
	public MultiMessage add(ChatMessage message, Object... formattingArgs) {
		this.chatMessages.add(message);
		this.formattingArgs.add(formattingArgs);
		return this;
	}
	
	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}
	
	public void send(ICommandSender sender) {
		if (chatMessages.isEmpty()) throw new IllegalArgumentException("Cannot send empty MultiMessage");
		ITextComponent send = null;
		for (int i = 0; i < chatMessages.size(); i++) {
			ChatMessage message = chatMessages.get(i);
			if (send == null) {
				send = message.getChatMessage(formattingArgs.get(i));
			} else {
				send.appendSibling(message.getChatMessage(formattingArgs.get(i)));
			}
		}
		sender.addChatMessage(send);
	}
	
	public List<Object[]> getFormattingArgs() {
		return formattingArgs;
	}
	
}
