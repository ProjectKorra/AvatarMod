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
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.gui.*;
import io.netty.buffer.ByteBuf;

import java.util.*;

/**
 * @author CrowsOfWar
 */
public class PacketSUnlockBending extends AvatarPacket<PacketSUnlockBending> {

	private byte type;

	public PacketSUnlockBending() {
	}

	public PacketSUnlockBending(UUID type) {
		this.type = BendingStyles.getNetworkId(type);
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		type = buf.readByte();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeByte(type);
	}

	public UUID getUnlockType() {
		return BendingStyles.get(type).getId();
	}

	public static class Handler extends AvatarPacketHandler<PacketSUnlockBending, IMessage> {
		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSUnlockBending message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			BendingData data = BendingData.get(player);
			Container container = player.openContainer;
			if (container instanceof ContainerGetBending) {
				UUID bending = message.getUnlockType();
				if (((ContainerGetBending) container).getEligibleBending().contains(bending)) {
					if (data.getAllBending().isEmpty()) {
						data.addBendingId(bending);
						// Unlock first ability
						// the ID is in use to unlock it
						Ability ability = Objects.requireNonNull(BendingStyles.get(bending)).getAllAbilities().get(0);
						data.getAbilityData(ability).unlockAbility();
						for (int i = 0; i < ((ContainerGetBending) container).getSize(); i++) {
							container.getSlot(i).putStack(ItemStack.EMPTY);
						}
						int guiId = AvatarGuiHandler.getGuiId(bending);
						player.openGui(AvatarMod.instance, guiId, player.world, 0, 0, 0);
					}
				}
			}
			return null;
		}
	}
}
