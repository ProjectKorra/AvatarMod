package com.crowsofwar.gorecore.chat;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatMessage {
	
	private final String translateKey;
	private final String[] translateArgs;
	private final MessageConfiguration config;
	
	private ChatMessage(MessageConfiguration config, String translateKey, String... translateArgs) {
		this.translateKey = translateKey;
		this.translateArgs = translateArgs;
		this.config = config;
	}
	
	public ITextComponent getChatMessage(Object... formattingArgs) {
		return new TextComponentTranslation(translateKey, formattingArgs);
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
	
	/**
	 * Creates a new chat message with the given translation key and formatting
	 * names.
	 * 
	 * @param translateKey
	 * @param translateArgs
	 * @return
	 */
	public static ChatMessage newChatMessage(String translateKey, String... translateArgs) {
		return newChatMessage(MessageConfiguration.DEFAULT, translateKey, translateArgs);
	}
	
	/**
	 * Creates a new chat message with the given translation key, formatting
	 * names, and configurations.
	 * 
	 * @param config
	 * @param translateKey
	 * @param translateArgs
	 * @return
	 */
	public static ChatMessage newChatMessage(MessageConfiguration config, String translateKey,
			String... translateArgs) {
		ChatMessage cm = new ChatMessage(config, translateKey, translateArgs);
		ChatSender.translateKeyToChatMessage.put(translateKey, cm);
		return cm;
	}
	
}