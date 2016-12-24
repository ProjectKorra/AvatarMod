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

import java.util.UUID;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;
import com.crowsofwar.avatar.common.network.packets.PacketSUseStatusControl;
import com.crowsofwar.gorecore.util.AccountUUIDs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Implements IPacketHandler. Acts as a packet handler for integrated and
 * dedicated servers. Is a singleton and is accessible via {@link #instance}.
 *
 */
public class PacketHandlerServer implements IPacketHandler {
	
	public static final IPacketHandler instance;
	
	static {
		instance = new PacketHandlerServer();
	}
	
	private PacketHandlerServer() {}
	
	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		AvatarLog.debug("Server: Received a packet");
		
		if (packet instanceof PacketSUseAbility) return handleKeypress((PacketSUseAbility) packet, ctx);
		
		if (packet instanceof PacketSRequestData) return handleRequestData((PacketSRequestData) packet, ctx);
		
		if (packet instanceof PacketSUseBendingController)
			return handleUseBendingController((PacketSUseBendingController) packet, ctx);
		
		if (packet instanceof PacketSUseStatusControl)
			return handleUseStatusControl((PacketSUseStatusControl) packet, ctx);
		
		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data != null) {
			
			BendingAbility ability = packet.getAbility();
			// TODO Verify that the client can actually use that ability
			ability.execute(new AbilityContext(data, packet.getRaytrace()));
			
		}
		
		return null;
	}
	
	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {
		
		UUID id = packet.getAskedPlayer();
		EntityPlayer player = AccountUUIDs
				.findEntityFromUUID(ctx.getServerHandler().playerEntity.worldObj, id);
		
		if (player == null) {
			
			AvatarLog.warnHacking(ctx.getServerHandler().playerEntity.getName(),
					"Sent request data for a player with account '" + id
							+ "', but that player is not in the world.");
			return null;
			
		}
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data != null) data.getNetworker().sendAll();
		return null;
		
	}
	
	// TODO remove packet s usebendingcontroller
	private IMessage handleUseBendingController(PacketSUseBendingController packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data != null) {
			
			if (data.hasBending(packet.getBendingControllerId())) {
			} else {
				AvatarLog.warn("Player '" + player.getName() + "' attempted to activate a BendingController "
						+ "they don't have; hacking?");
			}
			
		}
		
		return null;
	}
	
	/**
	 * @param packet
	 * @param ctx
	 * @return
	 */
	private IMessage handleUseStatusControl(PacketSUseStatusControl packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data != null) {
			StatusControl sc = packet.getStatusControl();
			if (data.hasStatusControl(sc)) {
				if (sc.execute(new AbilityContext(data, packet.getRaytrace()))) {
					
					data.removeStatusControl(packet.getStatusControl());
					data.sync();
					
				}
			}
			
		}
		
		return null;
	}
	
}
