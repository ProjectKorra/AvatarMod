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

package com.crowsofwar.avatar.util.command;

import com.crowsofwar.avatar.network.AvatarChatMessages;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeBranch;
import com.crowsofwar.gorecore.tree.TreeCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class AvatarCommand extends TreeCommand {

	public static final List<BendingStyle>[] CONTROLLER_BENDING_OPTIONS;
	public static final ITypeConverter<List<BendingStyle>> CONVERTER_BENDING = new ITypeConverter<List<BendingStyle>>() {

		@Override
		public List<BendingStyle> convert(String str) {
			return str.equals("all") ? BendingStyles.all()
					: Arrays.asList(BendingStyles.get(str.toLowerCase()));
		}

		@Override
		public String toString(List<BendingStyle> obj) {
			return obj.equals(BendingStyles.all()) ? "all" : obj.get(0).getName();
		}

		@Override
		public String getTypeName() {
			return "Bending";
		}

	};

	static {
		CONTROLLER_BENDING_OPTIONS = new List[BendingStyles.all().size() + 1];
		CONTROLLER_BENDING_OPTIONS[0] = BendingStyles.all();
		for (int i = 1; i < CONTROLLER_BENDING_OPTIONS.length; i++) {
			CONTROLLER_BENDING_OPTIONS[i] = Arrays.asList(BendingStyles.all().get(i - 1));
		}
	}

	public AvatarCommand() {
		super(AvatarChatMessages.CFG);
	}

	@Override
	public String getName() {
		return "avatar";
	}

	@Override
	protected ICommandNode[] addCommands() {

		NodeBendingList bendingList = new NodeBendingList();
		NodeBendingAdd bendingAdd = new NodeBendingAdd();
		NodeBendingRemove bendingRemove = new NodeBendingRemove();
		NodeBranch branchBending = new NodeBranch(AvatarChatMessages.MSG_BENDING_BRANCH_INFO, "bending",
				bendingList, bendingAdd, bendingRemove);

		NodeBranch branchAbility = new NodeBranch(branchHelpDefault, "ability", new NodeAbilityGet(),
				new NodeAbilitySet());

		return new ICommandNode[]{branchBending, new NodeConfig(), branchAbility, new NodeXpSet()};

	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {
		if (sender.canUseCommand(2, "avatarmod.command.avatar")) {
			super.execute(server, sender, arguments);
			AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onAvatarCommand());
		} else {
			TextComponentTranslation messagePermission = new TextComponentTranslation("commands.generic.permission");
			messagePermission.getStyle().setColor(TextFormatting.RED);
			sender.sendMessage(messagePermission);
		}
	}
}
