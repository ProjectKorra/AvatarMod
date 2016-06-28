package com.crowsofwar.avatar.common.network;

import java.util.UUID;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.AvatarPlayerDataFetcherServer;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSCheatEarthbending;
import com.crowsofwar.avatar.common.network.packets.PacketSCheckBendingList;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSToggleBending;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher.FetchDataResult;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import net.java.games.input.Controller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Implements IPacketHandler. Acts as a packet handler for
 * integrated and dedicated servers. Is a singleton and
 * is accessible via {@link #instance}.
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
		AvatarLog.debug("Recieved packet");
		
		if (packet instanceof PacketSCheckBendingList)
			return handleCheckBendingList((PacketSCheckBendingList) packet, ctx);
		
		if (packet instanceof PacketSCheatEarthbending)
			return handleCheatEarthbending((PacketSCheatEarthbending) packet, ctx);
		
		if (packet instanceof PacketSUseAbility)
			return handleKeypress((PacketSUseAbility) packet, ctx);
		
		if (packet instanceof PacketSToggleBending)
			return handleToggleBending((PacketSToggleBending) packet, ctx);
		
		if (packet instanceof PacketSRequestData)
			return handleRequestData((PacketSRequestData) packet, ctx);
		
		if (packet instanceof PacketSUseBendingController)
			return handleUseBendingController((PacketSUseBendingController) packet, ctx);
		
		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}

	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	private IMessage handleCheckBendingList(PacketSCheckBendingList packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		FetchDataResult result = AvatarPlayerDataFetcherServer.instance.getData(player);
		if (!result.hadError()) {
			AvatarPlayerData data = (AvatarPlayerData) result.getData();
			String display = "";
			for (int i = 0; i < data.getBendingControllers().size(); i++) {
				display += data.getBendingControllers().get(i) + ", ";
			}
			player.addChatMessage(new ChatComponentText("All bending abilities: " + display));
			
		} else {
			result.logError();
		}
		
		return null;
	}
	
	private IMessage handleCheatEarthbending(PacketSCheatEarthbending packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerDataFetcherServer.instance.
				getDataQuick(player, "Error while retrieving player data for Cheat Earthbending packet");
		if (data != null) {
			
			data.addBending(BendingManager.BENDINGID_EARTHBENDING);
			
		}
		
		return null;
	}
	
	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerDataFetcherServer.instance.
				getDataQuick(player, "Error while retrieving player data for Keypress packet");
		if (data != null) {
			IBendingController controller = data.getActiveBendingController();
			if (controller != null) {
				data.getState().update(player, packet.getTargetPos());
				controller.onAbility(packet.getAbility(), data);
			}
			
		}
		
		return null;
	}
	
	private IMessage handleToggleBending(PacketSToggleBending packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerDataFetcherServer.instance.
				getDataQuick(player, "Error while retrieving player data for Keypress packet");
		if (data != null) {
			
			if (data.isBending()) {
				data.setActiveBendingController(null);
				player.addChatMessage(new ChatComponentText("Bending toggled off"));
			} else {
				data.setActiveBendingController(data.getBendingController(BendingManager.BENDINGID_EARTHBENDING));
				player.addChatMessage(new ChatComponentText("Bending toggled on"));
			}
			
		}
		
		return null;
	}
	
	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {
		FetchDataResult result = AvatarPlayerDataFetcherServer.instance.getData(ctx.getServerHandler().playerEntity);
		if (result.hadError()) {
			AvatarLog.warn("Couldn't handle player data request packet because player data fetch had error");
			result.logError();
			return null;
		} else {
			return new PacketCPlayerData((AvatarPlayerData) result.getData());
		}
		
	}
	
	private IMessage handleUseBendingController(PacketSUseBendingController packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		FetchDataResult result = AvatarPlayerDataFetcherServer.instance.getData(player);
		
		if (result.hadError()) {
			AvatarLog.warn("Couldn't get " + player.getCommandSenderName() + "'s player data while handling "
					+ "UseBendingController packet");
			result.logError();
		} else {
			AvatarPlayerData data = (AvatarPlayerData) result.getData();
			
			if (data.hasBending(packet.getBendingControllerId())) {
				data.setActiveBendingController(packet.getBendingControllerId());
			} else {
				AvatarLog.warn("Player '" + player.getCommandSenderName() + "' attempted to activate a BendingController "
						+ "they don't have; hacking?");
			}
		
			
		}
		
		return null;
	}
	
}
