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

package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.network.PacketRedirector;
import com.crowsofwar.avatar.util.Raytrace;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public class PacketSUseStatusControl extends AvatarPacket<PacketSUseStatusControl> {

	private StatusControl statusControl;
	private Raytrace.Result raytrace;

	public PacketSUseStatusControl() {
	}

	public PacketSUseStatusControl(StatusControl control, Raytrace.Result raytrace) {
		this.statusControl = control;
		this.raytrace = raytrace;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		int id = buf.readInt();
		statusControl = StatusControlController.lookup(id);
		if (statusControl == null) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET,
					"Player trying to crash the server?? While sending UseStatusControl packet, sent invalid id "
							+ id);
			return; // TODO Cancel packet processing
		}

		raytrace = Raytrace.Result.fromBytes(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(statusControl.id());
		raytrace.toBytes(buf);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected AvatarPacket.Handler<PacketSUseStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public StatusControl getStatusControl() {
		return statusControl;
	}

	public Raytrace.Result getRaytrace() {
		return raytrace;
	}
}
