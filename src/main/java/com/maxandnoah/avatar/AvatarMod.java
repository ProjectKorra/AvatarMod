package com.maxandnoah.avatar;

import com.maxandnoah.avatar.common.AvatarCommonProxy;
import com.maxandnoah.avatar.common.bending.BendingManager;
import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;
import com.maxandnoah.avatar.common.network.packets.PacketSCheatEarthbending;
import com.maxandnoah.avatar.common.network.packets.PacketSCheckBendingList;
import com.maxandnoah.avatar.common.network.packets.PacketSKeypress;
import com.maxandnoah.avatar.common.network.packets.PacketSToggleBending;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = AvatarInfo.MOD_ID, name = AvatarInfo.MOD_NAME, version = AvatarInfo.VERSION)
public class AvatarMod {
	
	@SidedProxy(serverSide = "com.maxandnoah.avatar.server.AvatarServerProxy",
			clientSide = "com.maxandnoah.avatar.client.AvatarClientProxy")
	public static AvatarCommonProxy proxy;
	
	public static SimpleNetworkWrapper network;
	private int nextMessageID = 1;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit();
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MOD_ID + "_Network");
		registerPacket(PacketSCheckBendingList.class, Side.SERVER);
		registerPacket(PacketSCheatEarthbending.class, Side.SERVER);
		registerPacket(PacketSKeypress.class, Side.SERVER);
		registerPacket(PacketSToggleBending.class, Side.SERVER);
		
		BendingManager.init();
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		
	}
	
	private <REQ extends IAvatarPacket<REQ>> void registerPacket(Class<REQ> packet, Side side) {
		network.registerMessage(packet, packet, nextMessageID++, side);
	}
	
}
