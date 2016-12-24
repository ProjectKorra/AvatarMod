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

import java.util.UUID;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.AccountUUIDs.AccountID;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sent from client to server to request data about a player.
 *
 */
public class PacketSRequestData extends AvatarPacket<PacketSRequestData> {
	
	private UUID asking;
	
	public PacketSRequestData() {}
	
	public PacketSRequestData(UUID asking) {
		this.asking = asking;
	}
	
	public PacketSRequestData(EntityPlayer player) {
		AccountID result = AccountUUIDs.getUUID(player.getName());
		this.asking = result.getUUID();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		asking = GoreCoreByteBufUtil.readUUID(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, asking);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public UUID getAskedPlayer() {
		return asking;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketSRequestData> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
}
