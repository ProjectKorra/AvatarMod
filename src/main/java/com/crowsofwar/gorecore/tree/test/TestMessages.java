package com.crowsofwar.gorecore.tree.test;

import static com.crowsofwar.gorecore.chat.ChatMessage.newChatMessage;

import com.crowsofwar.gorecore.chat.ChatMessage;
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
	public static final ChatMessage MSG_FRUIT = newChatMessage(CFG, "test.chatSender", "fruit");
	public static final ChatMessage MSG_CONST = newChatMessage(CFG, "test.const");
	
}
