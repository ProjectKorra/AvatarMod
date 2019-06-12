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

import com.crowsofwar.avatar.common.network.PacketRedirector;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Aang23
 * 
 * Packet to sync Entity NBT Server -> Client(s)
 */
public class PacketCSyncEntityNBT extends AvatarPacket<PacketCSyncEntityNBT> {

	public int entityId;
	public NBTTagCompound data;

	public PacketCSyncEntityNBT() {
	}

	public PacketCSyncEntityNBT(int id, NBTTagCompound data) {
		this.entityId = id;
		this.data = data;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		ByteBufUtils.writeTag(buf, data);
	}

	@Override
	public Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected AvatarPacket.Handler<PacketCSyncEntityNBT> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
}
