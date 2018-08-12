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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import io.netty.buffer.ByteBuf;

/**
 * @author CrowsOfWar
 */
public class PacketSUseStatusControl extends AvatarPacket<PacketSUseStatusControl> {
	private StatusControl statusControl;
	private Raytrace.Result raytrace;

	public PacketSUseStatusControl() {
	}

	public PacketSUseStatusControl(StatusControl control, Raytrace.Result raytrace) {
		statusControl = control;
		this.raytrace = raytrace;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		int id = buf.readInt();
		statusControl = StatusControl.lookup(id);
		if (statusControl == null) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET,
						   "Player trying to crash the server?? While sending UseStatusControl packet, sent invalid id " + id);
			return; // TODO Cancel packet processing
		}

		raytrace = Raytrace.Result.fromBytes(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(statusControl.id());
		raytrace.toBytes(buf);
	}

	public StatusControl getStatusControl() {
		return statusControl;
	}

	public Raytrace.Result getRaytrace() {
		return raytrace;
	}

	public static class Handler extends AvatarPacketHandler<PacketSUseStatusControl, IMessage> {

		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSUseStatusControl message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			BendingData data = BendingData.get(player);
			StatusControl sc = message.getStatusControl();
			if (data.hasStatusControl(sc)) {
				if (sc.execute(new BendingContext(data, player, message.getRaytrace()))) {
					data.removeStatusControl(message.getStatusControl());
				}
			}

			return null;
		}
	}
}
