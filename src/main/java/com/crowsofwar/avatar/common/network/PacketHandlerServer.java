package com.crowsofwar.avatar.common.network;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Implements IPacketHandler. Acts as a packet handler for integrated and dedicated servers. Is a
 * singleton and is accessible via {@link #instance}.
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
		
		if (packet instanceof PacketSUseAbility) return handleKeypress((PacketSUseAbility) packet, ctx);
		
		if (packet instanceof PacketSRequestData) return handleRequestData((PacketSRequestData) packet, ctx);
		
		if (packet instanceof PacketSUseBendingController)
			return handleUseBendingController((PacketSUseBendingController) packet, ctx);
		
		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player,
				"Error while processing UseAbility packet");
		if (data != null) {
			BendingController controller = data.getActiveBendingController();
			if (controller != null) {
				data.getState().update(player, packet.getTargetPos(), packet.getSideHit());
				controller.onAbility(packet.getAbility(), data);
			}
			
		}
		
		return null;
	}
	
	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(ctx.getServerHandler().playerEntity,
				"Error while" + " processing RequestData packet");
		
		return data == null ? null : new PacketCPlayerData(data);
		
	}
	
	private IMessage handleUseBendingController(PacketSUseBendingController packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player,
				"Error while processing" + " UseBendingController packet");
		
		if (data != null) {
			
			if (data.hasBending(packet.getBendingControllerId())) {
				data.setActiveBendingController(packet.getBendingControllerId());
			} else {
				AvatarLog.warn("Player '" + player.getName() + "' attempted to activate a BendingController "
						+ "they don't have; hacking?");
			}
			
		}
		
		return null;
	}
	
}
