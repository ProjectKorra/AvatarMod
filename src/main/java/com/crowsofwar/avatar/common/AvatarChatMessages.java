package com.crowsofwar.avatar.common;

import static com.crowsofwar.gorecore.chat.ChatMessage.newChatMessage;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.chat.MessageConfiguration;

import net.minecraft.util.text.TextFormatting;

public class AvatarChatMessages {
	
	// @formatter:off
	public static final MessageConfiguration CFG = new MessageConfiguration()
			.addColor("value", TextFormatting.AQUA)
			.addColor("error", TextFormatting.RED)
			.addColor("error_value", TextFormatting.DARK_RED)
			.addColor("title", TextFormatting.GOLD);
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
	
	public static final ChatMessage MSG_CONFIG_EXCEPTION_1 = newChatMessage(CFG, "avatar.cmd.cfg.exception1");
	public static final ChatMessage MSG_CONFIG_EXCEPTION_2 = newChatMessage(CFG, "avatar.cmd.cfg.exception2", "details");
	public static final ChatMessage MSG_CONFIG_SUCCESS = newChatMessage(CFG, "avatar.cmd.cfg.successful");
	
	public static final ChatMessage MSG_ABILITY_SET_RANGE = newChatMessage(CFG, "avatar.cmd.ability.set.range");
	public static final ChatMessage MSG_ABILITY_SET_SUCCESS = newChatMessage(CFG, "avatar.cmd.ability.set.success", "player", "ability", "amount");
	
	public static final ChatMessage MSG_ABILITY_GET = newChatMessage(CFG, "avatar.cmd.ability.get", "player", "ability", "amount");
	
	public static final ChatMessage MSG_PROGRESS_POINT_ADDED = newChatMessage(CFG, "avatar.cmd.pp.add", "player", "pps");
	public static final ChatMessage MSG_PROGRESS_POINT_GET = newChatMessage(CFG, "avatar.cmd.pp.get", "player", "pps");
	public static final ChatMessage MSG_PROGRESS_POINT_SET = newChatMessage(CFG, "avatar.cmd.pp.set", "player", "pps");
	
	/**
	 * Call the static initializers
	 */
	public static void loadAll() {}
	
}
