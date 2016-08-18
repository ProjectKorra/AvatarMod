package com.crowsofwar.gorecore.chat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;

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
		IChatComponent send = null;
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
