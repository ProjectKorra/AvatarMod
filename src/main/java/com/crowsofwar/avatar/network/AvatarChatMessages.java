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

package com.crowsofwar.avatar.network;

import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.format.MessageConfiguration;
import net.minecraft.util.text.TextFormatting;

import static com.crowsofwar.gorecore.format.FormattedMessage.newChatMessage;

public class AvatarChatMessages {

	// @formatter:off
	public static final MessageConfiguration CFG = new MessageConfiguration()
			.addColor("value", TextFormatting.AQUA)
			.addColor("error", TextFormatting.RED)
			.addColor("error_value", TextFormatting.DARK_RED)
			.addColor("title", TextFormatting.GOLD);
	public static final FormattedMessage MSG_BENDING_BRANCH_INFO = newChatMessage(CFG, "avatar.cmd.bending");
	public static final FormattedMessage MSG_PLAYER_DATA_NO_DATA = newChatMessage(CFG, "avatar.cmd.bending.list.noData", "player");
	public static final FormattedMessage MSG_BENDING_LIST_NONBENDER = newChatMessage(CFG, "avatar.cmd.bending.list.nonbender", "player");
	public static final FormattedMessage MSG_BENDING_LIST_ITEM = newChatMessage(CFG, "avatar.cmd.bending.list.item", "bending");
	public static final FormattedMessage MSG_BENDING_LIST_TOP = newChatMessage(CFG, "avatar.cmd.bending.list.top", "player", "amount");

	public static final FormattedMessage MSG_BENDING_ADD_ALREADY_HAS = newChatMessage(CFG, "avatar.cmd.bending.add.alreadyHas", "player",
			"bending");
	public static final FormattedMessage MSG_BENDING_ADD_SUCCESS = newChatMessage(CFG, "avatar.cmd.bending.add.success", "player", "bending");

	public static final FormattedMessage MSG_BENDING_REMOVE_DOESNT_HAVE = newChatMessage(CFG, "avatar.cmd.bending.remove.doesntHave", "player",
			"bending");
	public static final FormattedMessage MSG_BENDING_REMOVE_SUCCESS = newChatMessage(CFG, "avatar.cmd.bending.remove.success", "player",
			"bending");

	public static final FormattedMessage MSG_CONFIG_EXCEPTION_1 = newChatMessage(CFG, "avatar.cmd.cfg.exception1");
	public static final FormattedMessage MSG_CONFIG_EXCEPTION_2 = newChatMessage(CFG, "avatar.cmd.cfg.exception2", "details");
	public static final FormattedMessage MSG_CONFIG_SUCCESS = newChatMessage(CFG, "avatar.cmd.cfg.successful");

	public static final FormattedMessage MSG_ABILITY_SET_RANGE = newChatMessage(CFG, "avatar.cmd.ability.set.range");
	public static final FormattedMessage MSG_ABILITY_SET_SUCCESS = newChatMessage(CFG, "avatar.cmd.ability.set.success", "player", "ability", "amount");

	public static final FormattedMessage MSG_ABILITY_GET = newChatMessage(CFG, "avatar.cmd.ability.get", "player", "ability", "amount");

	public static final FormattedMessage MSG_XPSET_SUCCESS = newChatMessage(CFG, "avatar.cmd.xpset", "player", "ability", "spec");


	public static final FormattedMessage MSG_DONT_HAVE_BENDING = newChatMessage(CFG, "avatar.donthavebending", "username");

	public static final FormattedMessage MSG_BISON_WHISTLE_SUMMON = newChatMessage(CFG, "avatar.bisonWhistle.summon", "time");
	public static final FormattedMessage MSG_BISON_WHISTLE_ASSIGN = newChatMessage(CFG, "avatar.bisonWhistle.assign", "bison");
	public static final FormattedMessage MSG_BISON_WHISTLE_NOSUMMON = newChatMessage(CFG, "avatar.bisonWhistle.notAssigned");
	public static final FormattedMessage MSG_BISON_WHISTLE_NOT_FOUND = newChatMessage(CFG, "avatar.bisonWhistle.notFound", "bison");
	public static final FormattedMessage MSG_BISON_WHISTLE_NOTOWNED = newChatMessage(CFG, "avatar.bisonWhistle.notOwned");
	public static final FormattedMessage MSG_BISON_WHISTLE_UNTAMED = newChatMessage(CFG, "avatar.bisonWhistle.untamed");
	public static final FormattedMessage MSG_BISON_WHISTLE_NEARBY = newChatMessage(CFG, "avatar.bisonWhistle.nearby");
	public static final FormattedMessage MSG_BISON_NO_FOOD = newChatMessage(CFG, "avatar.bisonNoFood");
	public static final FormattedMessage MSG_BISON_SITTING = newChatMessage(CFG, "avatar.bisonSitting");

	public static final FormattedMessage MSG_BISON_WHISTLE_FOLLOW_ON = newChatMessage(CFG, "avatar.bisonWhistle.followOn");
	public static final FormattedMessage MSG_BISON_WHISTLE_FOLLOW_OFF = newChatMessage(CFG, "avatar.bisonWhistle.followOff");

	public static final FormattedMessage MSG_SKY_BISON_STATS = newChatMessage(CFG, "avatar.bisonStats", "food", "health", "domestication");
	public static final FormattedMessage MSG_BISON_TRANSFER_OLD = newChatMessage(CFG, "avatar.bisonWhistle.transferAway", "bison", "newOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_NEW = newChatMessage(CFG, "avatar.bisonWhistle.transferTo", "bison", "oldOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_NONE = newChatMessage(CFG, "avatar.bisonWhistle.noTransfer");
	public static final FormattedMessage MSG_BISON_TRANSFER_OLD_START = newChatMessage(CFG, "avatar.bisonWhistle.transferAway.start", "bison", "newOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_NEW_START = newChatMessage(CFG, "avatar.bisonWhistle.transferTo.start", "bison", "oldOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_OLD_IGNORE = newChatMessage(CFG, "avatar.bisonWhistle.transferAway.ignore", "newOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_NEW_IGNORE = newChatMessage(CFG, "avatar.bisonWhistle.transferTo.ignore", "oldOwner");
	public static final FormattedMessage MSG_BISON_TRANSFER_OFFLINE = newChatMessage(CFG, "avatar.bisonWhistle.transferOffline", "owner");

	public static final FormattedMessage MSG_HUMANBENDER_NO_SCROLLS = newChatMessage(CFG, "avatar.outOfScrolls");

	public static final FormattedMessage MSG_NEED_TRADE_ITEM = newChatMessage(CFG, "avatar.needTradeItem");

	public static final FormattedMessage MSG_NEED_AIR_TRADE_ITEM = newChatMessage(CFG, "avatar.needAirTradeItem");

	public static final FormattedMessage MSG_NEED_FIRE_TRADE_ITEM = newChatMessage(CFG, "avatar.needFireTradeItem");

	public static final FormattedMessage MSG_AIR_STAFF_COOLDOWN = newChatMessage(CFG, "avatar.staffCooldown");

	public static final FormattedMessage MSG_SKATING_BENDING_DISABLED = newChatMessage(CFG,
			"avatar.skatingBendingDisabled");

	public static final FormattedMessage MSG_LIGHTNING_REDIRECT_SUCCESS = newChatMessage(CFG,
			"avatar.lightningRedirected", "lightningbender");

	public static final FormattedMessage MSG_CAN_UPGRADE_ABILITY = newChatMessage(CFG,
			"avatar.canUpgradeAbility", "ability", "newLevel");
	public static final FormattedMessage MSG_CAN_UPGRADE_ABILITY_2 = newChatMessage(CFG,
			"avatar.canUpgradeAbility2");
	public static final FormattedMessage MSG_CAN_UPGRADE_ABILITY_3 = newChatMessage(CFG,
			"avatar.canUpgradeAbility3", "bendingType");

	public static final FormattedMessage MSG_SPECIALTY_SCROLL_SUCCESS = newChatMessage(CFG, "avatar.specialtyScroll.success", "specialtyBending");
	public static final FormattedMessage MSG_SPECIALTY_SCROLL_FAIL = newChatMessage(CFG, "avatar.specialtyScroll.fail", "specialtyBending", "mainBending");
	public static final FormattedMessage MSG_SPECIALTY_SCROLL_ALREADY_HAVE = newChatMessage(CFG, "avatar.specialtyScroll.alreadyHave", "specialtyBending");
	public static final FormattedMessage MSG_SPECIALTY_SCROLL_TOOLTIP = newChatMessage(CFG, "avatar.specialtyScroll.tooltip", "bending");

	public static final FormattedMessage MSG_ANNOUNCEMENT_TODAY = newChatMessage(CFG, "avatar.announcement.today", "announcement");
	public static final FormattedMessage MSG_ANNOUNCEMENT_YESTERDAY = newChatMessage(CFG, "avatar.announcement.yesterday", "announcement");
	public static final FormattedMessage MSG_ANNOUNCEMENT_DAYS = newChatMessage(CFG, "avatar.announcement.days", "announcement", "days");

	/**
	 * Call the static initializers
	 */
	public static void loadAll() {
	}

}
