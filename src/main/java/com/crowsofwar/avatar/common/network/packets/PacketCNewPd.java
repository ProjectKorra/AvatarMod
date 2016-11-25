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

import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.readUUID;

import java.util.UUID;

import com.crowsofwar.avatar.common.network.Networker;
import com.crowsofwar.avatar.common.network.PacketModularData;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketCNewPd extends PacketModularData<PacketCNewPd> {
	
	private UUID playerId;
	
	public PacketCNewPd() {}
	
	public PacketCNewPd(Networker networker, UUID player) {
		super(networker);
		this.playerId = player;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerId = readUUID(buf);
		super.fromBytes(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, playerId);
		super.toBytes(buf);
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	@Override
	protected com.crowsofwar.avatar.common.network.packets.AvatarPacket.Handler<PacketCNewPd> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public UUID getPlayerId() {
		return playerId;
	}
	
}
