package com.crowsofwar.avatar.client;

import java.util.Random;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketCParticles;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCRemoveStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;

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
		
		if (packet instanceof PacketCPlayerData)
			return handlePacketPlayerData((PacketCPlayerData) packet, ctx);
		
		if (packet instanceof PacketCStatusControl)
			return handlePacketStatusControl((PacketCStatusControl) packet, ctx);
		
		if (packet instanceof PacketCRemoveStatusControl)
			return handlePacketStatusControl((PacketCRemoveStatusControl) packet, ctx);
		
		if (packet instanceof PacketCParticles) return handlePacketParticles((PacketCParticles) packet, ctx);
		
		AvatarLog.warn(WarningType.WEIRD_PACKET, "Client recieved unknown packet from server:" + packet);
		
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	private IMessage handlePacketPlayerData(PacketCPlayerData packet, MessageContext ctx) {
		EntityPlayer player = GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(mc.theWorld, packet.getPlayer());
		if (player == null) {
			AvatarLog.warn(WarningType.WEIRD_PACKET,
					"Recieved player data packet about a player, but the player couldn't be found. Is he unloaded?");
			AvatarLog.warn(WarningType.WEIRD_PACKET, "The player ID was: " + packet.getPlayer());
			return null;
		}
		AvatarLog.debug("Client: Received data packet for " + player);
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player,
				"Error while processing player data packet");
		if (data != null) {
			// Add bending controllers & bending states
			data.takeBending();
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				data.addBending(packet.getAllControllersID()[i]);
				data.getState().update(player, Raytrace.getTargetBlock(player, -1));
			}
			data.setAbilityData(packet.getAbilityData());
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				IBendingState state = data.getBendingState(packet.getBuf().readInt());
				state.fromBytes(packet.getBuf());
			}
			
		}
		return null;
	}
	
	private IMessage handlePacketStatusControl(PacketCStatusControl packet, MessageContext ctx) {
		AvatarMod.proxy.addStatusControl(packet.getStatusControl());
		return null;
	}
	
	private IMessage handlePacketStatusControl(PacketCRemoveStatusControl packet, MessageContext ctx) {
		AvatarMod.proxy.removeStatusControl(packet.getStatusControl());
		return null;
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
	
}
