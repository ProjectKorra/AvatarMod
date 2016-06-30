package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketCControllingBlock;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCThrownBlockVelocity;
import com.crowsofwar.avatar.common.util.Raytrace;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher.FetchDataResult;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Handles packets addressed to the client. Packets like
 * this have a C in their name.
 *
 */
@SideOnly(Side.CLIENT)
public class PacketHandlerClient implements IPacketHandler {

	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		if (packet instanceof PacketCThrownBlockVelocity)
			return handlePacketThrownBlockVelocity((PacketCThrownBlockVelocity) packet, ctx);
		
		if (packet instanceof PacketCControllingBlock)
			return handlePacketControllingBlock((PacketCControllingBlock) packet, ctx);
		
		if (packet instanceof PacketCPlayerData)
			return handlePacketPlayerData((PacketCPlayerData) packet, ctx);
		
		AvatarLog.warn("Client recieved unknown packet from server:" + packet);
		
		return null;
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	private IMessage handlePacketThrownBlockVelocity(PacketCThrownBlockVelocity packet, MessageContext ctx) {
		World world = Minecraft.getMinecraft().theWorld;
		EntityFloatingBlock floating = EntityFloatingBlock.getFromID(world, packet.getBlockID());
		if (floating != null) {
			System.out.println("Set velocity on client");
			floating.setVelocity(packet.getVelocity());
		} else {
			AvatarLog.warn("PacketCThrownBlockVelocity- No block found with ID " + packet.getBlockID());
		}
		return null;
	}
	
	// TODO remove this packet
	private IMessage handlePacketControllingBlock(PacketCControllingBlock packet, MessageContext ctx) {
//		System.out.println("handlin pakit");
//		World world = Minecraft.getMinecraft().theWorld;
//		EntityFloatingBlock floating = EntityFloatingBlock.getFromID(world, packet.getFloatingBlockID());
////		AvatarClientTick.instance.floating = floating;
//		AvatarPlayerData data = AvatarPlayerDataFetcherClient.instance.getDataQuick(Minecraft.getMinecraft().thePlayer, "E");
//		if (data != null) {
//			if (data.getActiveBendingController() instanceof Earthbending) {
//				((EarthbendingState) data.getBendingState()).setPickupBlock(floating);
//			}
//		}
		
		return null;
	}
	
	private IMessage handlePacketPlayerData(PacketCPlayerData packet, MessageContext ctx) {
		System.out.println("recieved player data information for " + packet.getPlayer());
		FetchDataResult result = AvatarPlayerDataFetcherClient.instance.getData(Minecraft.getMinecraft().thePlayer);
		if (result.hadError()) {
			AvatarLog.warn("Couldn't handle player data info because player data fetch had error");
			result.logError();
		} else {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			AvatarPlayerData data = (AvatarPlayerData) result.getData();
			// Add bending controllers & bending states
			data.takeBending();
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				data.addBending(packet.getAllControllersID()[i]);
				data.getState().update(player, Raytrace.getTargetBlock(player, -1));
			}
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				IBendingState state = data.getBendingController(packet.getBuf().readInt()).createState(data);
				state.fromBytes(packet.getBuf());
			}
			
			data.setActiveBendingController(BendingManager.getBending(packet.getCurrentBendingControllerID()));
		}
		return null;
	}
	
}
