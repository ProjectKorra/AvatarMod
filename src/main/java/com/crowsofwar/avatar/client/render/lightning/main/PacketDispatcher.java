package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.AvatarInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDispatcher {
	
	public static final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MOD_ID);

	public static void registerPackets(){
		int i = 0;
		wrapper.registerMessage(AuxParticlePacketNT.Handler.class, AuxParticlePacketNT.class, i++, Side.CLIENT);
		wrapper.registerMessage(AuxButtonPacket.Handler.class, AuxButtonPacket.class, i++, Side.SERVER);
		wrapper.registerMessage(PacketSpecialDeath.Handler.class, PacketSpecialDeath.class, i++, Side.CLIENT);

	}

	public static void sendTo(IMessage message, EntityPlayerMP player){
		if(player != null)
			wrapper.sendTo(message, player);
	}

}
