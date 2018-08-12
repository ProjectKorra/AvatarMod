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
package com.crowsofwar.avatar.common.network.packets;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class PacketSSkillsMenu extends AvatarPacket<PacketSSkillsMenu> {
	private byte element;
	private int abilityId;

	public PacketSSkillsMenu() {
	}

	public PacketSSkillsMenu(UUID element) {
		this(element, null);
	}

	public PacketSSkillsMenu(UUID element, @Nullable Ability ability) {
		this.element = BendingStyles.getNetworkId(element);
		if (ability == null) {
			abilityId = -1;
		} else {
			abilityId = Abilities.all().indexOf(ability);
		}
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		element = buf.readByte();
		abilityId = buf.readInt();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeByte(element);
		buf.writeInt(abilityId);
	}

	public UUID getElement() {
		return BendingStyles.get(element).getId();
	}

	@Nullable
	public Ability getAbility() {
		if (abilityId == -1) {
			return null;
		}
		return Abilities.all().get(abilityId);
	}

	public static class Handler extends AvatarPacketHandler<PacketSSkillsMenu, IMessage> {
		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSSkillsMenu message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			UUID element = message.getElement();
			if (BendingStyles.has(element)) {
				if (BendingData.get(player).hasBendingId(element)) {
					player.openGui(AvatarMod.instance, AvatarGuiHandler.getGuiId(element), player.world, 0, 0, 0);
					if (message.getAbility() != null) {
						return new PacketCOpenSkillCard(message.getAbility());
					}
				}
			}
			return null;
		}
	}

}
