package com.crowsofwar.avatar.client;

import static com.crowsofwar.avatar.common.data.AvatarPlayerData.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.Networker;
import com.crowsofwar.avatar.common.network.PlayerDataContext;
import com.crowsofwar.avatar.common.network.packets.PacketCNewPd;
import com.crowsofwar.avatar.common.network.packets.PacketCParticles;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCRemoveStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;

import io.netty.buffer.ByteBuf;
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
		
		if (packet instanceof PacketCNewPd) return handlePacketNewPlayerData((PacketCNewPd) packet, ctx);
		
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
			
			// Add bending controllers
			data.takeBending();
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				data.addBending(packet.getAllControllersID()[i]);
				data.getState().update(player, Raytrace.getTargetBlock(player, -1));
			}
			
			// Read ability data
			data.clearAbilityData();
			ByteBuf buf = packet.getBuf();
			int abilitiesAmount = buf.readInt();
			for (int i = 0; i < abilitiesAmount; i++) {
				AbilityData abilityData = data.getAbilityData(BendingManager.getAbility(buf.readInt()));
				abilityData.setXp(buf.readFloat());
			}
			
			// Read bending states
			int lastIndex = buf.readerIndex();
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				BendingState state = data.getBendingState(packet.getBuf().readInt());
				state.fromBytes(packet.getBuf());
				System.out
						.println("State: " + state + ": read  " + (buf.readerIndex() - lastIndex) + " bytes");
				lastIndex = buf.readerIndex();
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
	
	/**
	 */
	private IMessage handlePacketNewPlayerData(PacketCNewPd packet, MessageContext ctx) {
		
		EntityPlayer player = GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(mc.theWorld,
				packet.getPlayerId());
		if (player == null) {
			AvatarLog.warn(WarningType.WEIRD_PACKET,
					"Recieved player data packet about a player, but the player couldn't be found. Is he unloaded?");
			AvatarLog.warn(WarningType.WEIRD_PACKET, "The player ID was: " + packet.getPlayerId());
			return null;
		}
		AvatarLog.debug("Client: Received data packet for " + player);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player,
				"Error while processing player data packet");
		if (data != null) {
			
			Map<Networker.Property, Object> readData = packet.interpretData(data.getNetworker(),
					new PlayerDataContext(data));
			if (readData.containsKey(KEY_CONTROLLERS)) {
				data.takeBending();
				System.out.println("Currently all are " + data.getBendingControllers());
				List<BendingController> bending = (List<BendingController>) readData.get(KEY_CONTROLLERS);
				for (BendingController controller : bending) {
					data.addBending(controller);
					System.out.println("Add " + controller);
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
			
		}
		
		return null;
	}
	
}
