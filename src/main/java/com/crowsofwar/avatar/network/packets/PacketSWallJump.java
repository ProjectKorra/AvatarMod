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

import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public class PacketSWallJump extends AvatarPacket<PacketSWallJump> {

	//private KeyBinding key;
	private int direction;

	public PacketSWallJump(int direction) {
		this.direction = direction;
	}

	public PacketSWallJump() {
		super();
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
	//	key = getKeyFromID(buf.readInt());
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		//buf.writeInt(direction);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected com.crowsofwar.avatar.network.packets.AvatarPacket.Handler<PacketSWallJump> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	/*private KeyBinding getKeyFromID(int id) {
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		switch (id) {
			case 0:
				return settings.keyBindForward;
			case 1:
				return settings.keyBindRight;
			case 2:
				return settings.keyBindBack;
			case 3:
				return settings.keyBindLeft;
			default:
				break;
		}
		return settings.keyBindForward;
	}**/

	/*public KeyBinding getKey() {
		return this.key;
	}**/

}
