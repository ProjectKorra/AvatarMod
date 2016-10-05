package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles packets addressed to the client. Packets like this have a C in their name.
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
		
		AvatarLog.warn("Client recieved unknown packet from server:" + packet);
		
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	private IMessage handlePacketPlayerData(PacketCPlayerData packet, MessageContext ctx) {
		EntityPlayer player = GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(mc.theWorld, packet.getPlayer());
		if (player == null) {
			AvatarLog.warn(
					"Recieved player data packet about a player, but the player couldn't be found. Is he unloaded?");
			AvatarLog.warn("The player ID was: " + packet.getPlayer());
			return null;
		}
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player,
				"Error while processing player data packet");
		if (data != null) {
			// Add bending controllers & bending states
			data.takeBending();
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				data.addBending(packet.getAllControllersID()[i]);
				data.getState().update(player, Raytrace.getTargetBlock(player, -1));
			}
			for (int i = 0; i < packet.getAllControllersID().length; i++) {
				IBendingState state = data.getBendingState(packet.getBuf().readInt());
				state.fromBytes(packet.getBuf());
			}
			
			data.setActiveBendingController(
					BendingManager.getBending(packet.getCurrentBendingControllerID()));
		}
		return null;
	}
	
	private IMessage handlePacketStatusControl(PacketCStatusControl packet, MessageContext ctx) {
		// TODO add status image to crosshair...
		System.out.println("Adding status control " + packet.getStatusControl());
		AvatarMod.proxy.addStatusControl(packet.getStatusControl());
		return null;
	}
	
}
