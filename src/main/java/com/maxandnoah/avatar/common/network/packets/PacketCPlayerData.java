package com.maxandnoah.avatar.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.maxandnoah.avatar.common.bending.IBendingController;
import com.maxandnoah.avatar.common.bending.IBendingState;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import io.netty.buffer.ByteBuf;

/**
 * Sent from server to client to notify the client of
 * a player's current bending controller.
 *
 */
public class PacketCPlayerData implements IAvatarPacket<PacketCPlayerData> {
	
	private UUID player;
	private int[] allControllers;
	private int controllerID;
	private List<IBendingState> states;
	private ByteBuf buffer;
	
	public PacketCPlayerData() {}
	
	public PacketCPlayerData(AvatarPlayerData data) {
		player = data.getPlayerID();
		allControllers = new int[data.getBendingControllers().size()];
		for (int i = 0; i < allControllers.length; i++) {
			allControllers[i] = data.getBendingControllers().get(i).getID();
		}
		controllerID = data.isBending() ? data.getActiveBendingController().getID() : -1;
		states = data.getAllBendingStates();
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		player = GoreCoreByteBufUtil.readUUID(buf);
		int length = buf.readInt();
		allControllers = new int[length];
		for (int i = 0; i < length; i++) allControllers[i] = buf.readInt();
		controllerID = buf.readInt();
		buffer = buf;
//		state.fromBytes(buf);
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, player);
		buf.writeInt(allControllers.length);
		for (int i = 0; i < allControllers.length; i++) buf.writeInt(allControllers[i]);
		buf.writeInt(controllerID);
		for (IBendingState state : states) {
			state.toBytes(buf);
		}
	}

	@Override
	public IMessage onMessage(PacketCPlayerData message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

	@Override
	public Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	/**
	 * Get an array of the Ids of all the player's bending controllers.
	 */
	public int[] getAllControllersID() {
		return allControllers;
	}
	
	public int getCurrentBendingControllerID() {
		return controllerID;
	}
	
	public ByteBuf getBuf() {
		return buffer;
	}
	
	public int getIndex() {
		return buffer.readerIndex();
	}
	
}
