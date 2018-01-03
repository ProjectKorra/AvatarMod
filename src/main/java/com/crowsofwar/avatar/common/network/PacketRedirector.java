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

package com.crowsofwar.avatar.common.network;

import com.crowsofwar.avatar.AvatarMod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Redirect a packet to the correct sided packet handler. Contains a single
 * method, {@link #redirectMessage(IMessage, MessageContext)}. Not to be
 * instantiated.
 */
public class PacketRedirector {

	/**
	 * Only use static methods. Not to be instantiated.
	 */
	private PacketRedirector() {
	}

	public static IMessage redirectMessage(IMessage message, MessageContext ctx) {
		IPacketHandler packetHandler;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			packetHandler = AvatarMod.proxy.getClientPacketHandler();
		} else {
			packetHandler = PacketHandlerServer.instance;
		}
		return packetHandler.onPacketReceived(message, ctx);
	}

}
