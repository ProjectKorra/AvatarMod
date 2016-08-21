package com.crowsofwar.avatar.common.network.packets;

import java.util.List;
import java.util.UUID;

import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sent from server to client to notify the client of a player's current bending controller.
 *
 * @author CrowsOfWar
 */
public class PacketCPlayerData extends AvatarPacket<PacketCPlayerData> {
	
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
		controllerID = buf.readInt();
		// Read bending controllers
		allControllers = new int[buf.readInt()];
		for (int i = 0; i < allControllers.length; i++) {
			allControllers[i] = buf.readInt();
		}
		
		// Reading bending states is done elsewhere
		buffer = buf;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, player);
		buf.writeInt(controllerID);
		buf.writeInt(allControllers.length);
		// Write bending controllers
		for (int i = 0; i < allControllers.length; i++) {
			buf.writeInt(allControllers[i]);
		}
		// Write bending states
		for (int i = 0; i < states.size(); i++) {
			buf.writeInt(states.get(i).getId());
			states.get(i).toBytes(buf);
		}
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
	
	@Override
	protected AvatarPacket.Handler<PacketCPlayerData> getPacketHandler() {
		return (message, context) -> {
			PacketRedirector.redirectMessage(message, context);
		};
	}
	
}
