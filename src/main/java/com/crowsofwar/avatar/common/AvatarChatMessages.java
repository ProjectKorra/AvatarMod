package com.crowsofwar.avatar.common;

import static crowsofwar.gorecore.chat.ChatSender.newChatMessage;

import crowsofwar.gorecore.chat.ChatMessage;
import crowsofwar.gorecore.chat.MessageConfiguration;
import net.minecraft.util.EnumChatFormatting;

public class AvatarChatMessages {
	
	public static final MessageConfiguration CFG = new MessageConfiguration().addColor("value", EnumChatFormatting.AQUA)
			.addColor("error", EnumChatFormatting.RED).addColor("error_value", EnumChatFormatting.DARK_RED);
	public static final ChatMessage MSG_BENDING_BRANCH_INFO = newChatMessage(CFG, "avatar.cmd.bending");
	public static final ChatMessage MSG_PLAYER_DATA_NO_DATA = newChatMessage(CFG, "avatar.cmd.bending.list.noData", "player");
	public static final ChatMessage MSG_BENDING_LIST_NONBENDER = newChatMessage(CFG, "avatar.cmd.bending.list.nonbender", "player");
	public static final ChatMessage MSG_BENDING_LIST_ITEM = newChatMessage(CFG, "avatar.cmd.bending.list.item", "bending");
	public static final ChatMessage MSG_BENDING_LIST_TOP = newChatMessage(CFG, "avatar.cmd.bending.list.top", "player", "amount");
	
	public static final ChatMessage MSG_BENDING_ADD_ALREADY_HAS = newChatMessage(CFG, "avatar.cmd.bending.add.alreadyHas", "player",
			"bending");
	public static final ChatMessage MSG_BENDING_ADD_SUCCESS = newChatMessage(CFG, "avatar.cmd.bending.add.success", "player", "bending");
	
	public static final ChatMessage MSG_BENDING_REMOVE_DOESNT_HAVE = newChatMessage(CFG, "avatar.cmd.bending.remove.doesntHave", "player",
			"bending");
	public static final ChatMessage MSG_BENDING_REMOVE_SUCCESS = newChatMessage(CFG, "avatar.cmd.bending.remove.success", "player",
			"bending");
	
	public static final ChatMessage MSG_EARTHBENDING = newChatMessage(CFG, "avatar.earthbending");
	public static final ChatMessage MSG_FIREBENDING = newChatMessage(CFG, "avatar.firebending");
	public static final ChatMessage MSG_WATERBENDING = newChatMessage(CFG, "avatar.waterbending");
	
}
