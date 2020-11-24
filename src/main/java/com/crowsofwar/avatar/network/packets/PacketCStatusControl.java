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

import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet from server -> client to add a status control to the crosshair.
 *
 * @author CrowsOfWar
 */
public class PacketCStatusControl extends AvatarPacket<PacketCStatusControl> {

	private StatusControl control;

	public PacketCStatusControl() {
	}

	public PacketCStatusControl(StatusControl control) {
		this.control = control;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		control = StatusControlController.lookup(buf.readInt());
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(control.id());
	}

	@Override
	protected Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected AvatarPacket.Handler<PacketCStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public StatusControl getStatusControl() {
		return control;
	}

}
