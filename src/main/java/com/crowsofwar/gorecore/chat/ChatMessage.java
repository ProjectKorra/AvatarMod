package com.crowsofwar.gorecore.chat;

import net.minecraft.command.ICommandSender;

public class ChatMessage {
	
	private final String translateKey;
	private final String[] translateArgs;
	private final MessageConfiguration config;
	
	ChatMessage(MessageConfiguration config, String translateKey, String... translateArgs) {
		this.translateKey = translateKey;
		this.translateArgs = translateArgs;
		this.config = config;
	}
	
	public IChatComponent getChatMessage(Object... formattingArgs) {
		return new ChatComponentTranslation(translateKey, formattingArgs);
	}
	
	public void send(ICommandSender sender, Object... formattingArgs) {
		ChatSender.send(sender, this, formattingArgs);
	}
	
	public String[] getTranslationArgs() {
		return translateArgs;
	}
	
	public MultiMessage chain() {
		return new MultiMessage().add(this);
	}
	
	public MessageConfiguration getConfig() {
		return config;
	}
	
}