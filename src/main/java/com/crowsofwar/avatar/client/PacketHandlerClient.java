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

package com.crowsofwar.avatar.client;

import static com.crowsofwar.avatar.common.data.AvatarPlayerData.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.Networker;
import com.crowsofwar.avatar.common.network.PlayerDataContext;
import com.crowsofwar.avatar.common.network.packets.PacketCParticles;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.gorecore.util.AccountUUIDs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles packets addressed to the client. Packets like this have a C in their
 * name.
 *
 */
@SideOnly(Side.CLIENT)
public class PacketHandlerClient implements IPacketHandler {
	
	private final Minecraft mc;
	
	public PacketHandlerClient() {
		this.mc = Minecraft.getMinecraft();
	}
	
	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		
		if (packet instanceof PacketCParticles) return handlePacketParticles((PacketCParticles) packet, ctx);
		
		if (packet instanceof PacketCPlayerData)
			return handlePacketNewPlayerData((PacketCPlayerData) packet, ctx);
		
		AvatarLog.warn(WarningType.WEIRD_PACKET, "Client recieved unknown packet from server:" + packet);
		
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	private IMessage handlePacketParticles(PacketCParticles packet, MessageContext ctx) {
		
		EnumParticleTypes particle = packet.getParticle();
		if (particle == null) {
			AvatarLog.warn(WarningType.WEIRD_PACKET, "Unknown particle recieved from server");
			return null;
		}
		
		Random random = new Random();
		
		int particles = random.nextInt(packet.getMaximum() - packet.getMinimum() + 1) + packet.getMinimum();
		
		for (int i = 0; i < particles; i++) {
			mc.theWorld.spawnParticle(particle, packet.getX(), packet.getY(), packet.getZ(),
					packet.getMaxVelocityX() * random.nextGaussian(),
					packet.getMaxVelocityY() * random.nextGaussian(),
					packet.getMaxVelocityZ() * random.nextGaussian());
		}
		
		return null;
	}
	
	/**
	 */
	private IMessage handlePacketNewPlayerData(PacketCPlayerData packet, MessageContext ctx) {
		
		EntityPlayer player = AccountUUIDs.findPlayerInWorldFromUUID(mc.theWorld, packet.getPlayerId());
		if (player == null) {
			AvatarLog.warn(WarningType.WEIRD_PACKET,
					"Recieved player data packet about a player, but the player couldn't be found. Is he unloaded?");
			AvatarLog.warn(WarningType.WEIRD_PACKET, "The player ID was: " + packet.getPlayerId());
			return null;
		}
		AvatarLog.debug("Client: Received data packet for " + player);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data != null) {
			
			Map<Networker.Property, Object> readData = packet.interpretData(data.getNetworker(),
					new PlayerDataContext(data));
			if (readData.containsKey(KEY_CONTROLLERS)) {
				data.takeBending();
				List<BendingController> bending = (List<BendingController>) readData.get(KEY_CONTROLLERS);
				for (BendingController controller : bending) {
					data.addBending(controller);
				}
			}
			
			if (readData.containsKey(KEY_STATES)) {
				data.clearBendingStates();
				for (BendingState state : (List<BendingState>) readData.get(KEY_STATES))
					data.addBendingState(state);
			}
			
			if (readData.containsKey(KEY_ABILITY_DATA)) {
				data.clearAbilityData();
				Set<Map.Entry<BendingAbility, AbilityData>> entries = ((Map<BendingAbility, AbilityData>) readData
						.get(KEY_ABILITY_DATA)).entrySet();
				for (Map.Entry<BendingAbility, AbilityData> entry : entries) {
					data.getAbilityData(entry.getKey()).setXp(entry.getValue().getXp());
				}
			}
			
			if (readData.containsKey(KEY_STATUS_CONTROLS)) {
				data.clearStatusControls();
				Set<StatusControl> controls = (Set<StatusControl>) readData.get(KEY_STATUS_CONTROLS);
				for (StatusControl control : controls) {
					data.addStatusControl(control);
				}
			}
			
		}
		
		return null;
	}
	
}
