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
	
	public static final ChatMessage MSG_PROGRESS_POINT_ADDED = newChatMessage(CFG, "avatar.cmd.pp.add", "player", "pps", "bending");
	public static final ChatMessage MSG_PROGRESS_POINT_GET = newChatMessage(CFG, "avatar.cmd.pp.get", "player", "pps", "bending");
	public static final ChatMessage MSG_PROGRESS_POINT_SET = newChatMessage(CFG, "avatar.cmd.pp.set", "player", "pps", "bending");
	public static final ChatMessage MSG_PROGRESS_POINT_SET_RANGE = newChatMessage(CFG, "avatar.cmd.pp.set.range");
	
	public static final ChatMessage MSG_DONT_HAVE_BENDING = newChatMessage(CFG, "avatar.donthavebending", "bending", "username");
	
	public static final ChatMessage MSG_BISON_WHISTLE_SUMMON = newChatMessage(CFG, "avatar.bisonWhistle.summon", "time");
	public static final ChatMessage MSG_BISON_WHISTLE_ASSIGN = newChatMessage(CFG, "avatar.bisonWhistle.assign", "bison");
	public static final ChatMessage MSG_BISON_WHISTLE_NOSUMMON = newChatMessage(CFG, "avatar.bisonWhistle.notAssigned");
	public static final ChatMessage MSG_BISON_WHISTLE_NOT_FOUND = newChatMessage(CFG, "avatar.bisonWhistle.notFound", "bison");
	public static final ChatMessage MSG_BISON_WHISTLE_NOTOWNED = newChatMessage(CFG, "avatar.bisonWhistle.notOwned");
	public static final ChatMessage MSG_SKY_BISON_STATS = newChatMessage(CFG, "avatar.bisonStats", "food", "health", "domestication");
	public static final ChatMessage MSG_BISON_TRANSFER_OLD = newChatMessage(CFG, "avatar.bisonWhistle.transferAway", "bison", "newOwner");
	public static final ChatMessage MSG_BISON_TRANSFER_NEW = newChatMessage(CFG, "avatar.bisonWhistle.transferTo", "bison", "oldOwner");
	public static final ChatMessage MSG_BISON_TRANSFER_NONE = newChatMessage(CFG, "avatar.bisonWhistle.noTransfer");
	
	public static final ChatMessage MSG_HUMANBENDER_NO_SCROLLS = newChatMessage(CFG, "avatar.outOfScrolls");
	
	/**
	 * Call the static initializers
	 */
	public static void loadAll() {}
	
}
