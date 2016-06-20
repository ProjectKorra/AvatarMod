package com.maxandnoah.avatar.common.network;

import java.util.UUID;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.data.AvatarPlayerDataFetcherServer;
import com.maxandnoah.avatar.common.network.packets.PacketSCheatEarthbending;
import com.maxandnoah.avatar.common.network.packets.PacketSCheckBendingList;
import com.maxandnoah.avatar.common.network.packets.PacketSKeypress;
import com.maxandnoah.avatar.common.network.packets.PacketSToggleBending;
import com.maxandnoah.avatar.common.bending.BendingController;
import com.maxandnoah.avatar.common.bending.BendingManager;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher.FetchDataResult;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

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
		
		if (packet instanceof PacketSKeypress)
			return handleKeypress((PacketSKeypress) packet, ctx);
		
		if (packet instanceof PacketSToggleBending)
			return handleToggleBending((PacketSToggleBending) packet, ctx);
		
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
			
			System.out.println("Adding earthbending");
			data.addBending(BendingManager.BENDINGID_EARTHBENDING);
			System.out.println(data.getBendingControllers());
			
		}
		
		System.out.println("Done");
		return null;
	}
	
	private IMessage handleKeypress(PacketSKeypress packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerDataFetcherServer.instance.
				getDataQuick(player, "Error while retrieving player data for Keypress packet");
		if (data != null) {
			BendingController controller = data.getActiveBendingController();
			System.out.println("Active controller: " + controller);
			if (controller != null) {
				controller.onKeypress(packet.getControlPressed(), player, data);
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
	
}
