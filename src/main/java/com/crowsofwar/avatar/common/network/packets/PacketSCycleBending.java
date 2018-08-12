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

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.data.BendingData;
import io.netty.buffer.ByteBuf;

import java.util.*;

/**
 * @author CrowsOfWar
 */
public class PacketSCycleBending extends AvatarPacket<PacketSCycleBending> {

	private boolean right;

	public PacketSCycleBending() {
	}

	public PacketSCycleBending(boolean right) {
		this.right = right;
	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {
		right = buf.readBoolean();
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeBoolean(right);
	}

	public boolean cycleRight() {
		return right;
	}

	public static class Handler extends AvatarPacketHandler<PacketSCycleBending, IMessage> {

		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSCycleBending message, MessageContext ctx) {
			BendingData data = BendingData.get(ctx.getServerHandler().player);
			List<BendingStyle> controllers = data.getAllBending();
			controllers.sort(Comparator.comparing(BendingStyle::getName));
			if (controllers.size() > 1) {
				int index = controllers.indexOf(data.getActiveBending());
				index += message.cycleRight() ? 1 : -1;

				if (index == -1) index = controllers.size() - 1;
				if (index == controllers.size()) index = 0;

				data.setActiveBending(controllers.get(index));
			}
			return null;
		}
	}
}
