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

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import io.netty.buffer.ByteBuf;

/**
 * @author CrowsOfWar
 */
public abstract class AvatarPacket<MSG extends AvatarPacket> implements IMessage {
	public AvatarPacket() {
	}

	@Override
	public final void fromBytes(ByteBuf buf) {
		try {
			avatarFromBytes(buf);
		} catch (RuntimeException ex) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET, "Error processing packet " + getClass().getSimpleName(), ex);
		}
	}

	@Override
	public final void toBytes(ByteBuf buf) {
		try {
			avatarToBytes(buf);
		} catch (RuntimeException ex) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET, "Error processing packet " + getClass().getSimpleName(), ex);
		}
	}

	protected abstract void avatarFromBytes(ByteBuf buf);

	protected abstract void avatarToBytes(ByteBuf buf);
}
