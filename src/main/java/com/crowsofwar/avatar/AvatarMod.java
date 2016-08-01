package com.crowsofwar.avatar;

import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarPlayerTick;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.command.AvatarCommand;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlame;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.IAvatarPacket;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;

@Mod(modid = AvatarInfo.MOD_ID, name = AvatarInfo.MOD_NAME, version = AvatarInfo.VERSION)
public class AvatarMod {
	
	@SidedProxy(serverSide = "com.crowsofwar.avatar.server.AvatarServerProxy",
			clientSide = "com.crowsofwar.avatar.client.AvatarClientProxy")
	public static AvatarCommonProxy proxy;
	
	@Instance(value=AvatarInfo.MOD_ID)
	public static AvatarMod instance;
	
	public static SimpleNetworkWrapper network;
	
	private int nextMessageID = 1;
	private int nextEntityID = 1;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit();
		AvatarPlayerData.initFetcher(proxy.getClientDataFetcher());
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MOD_ID + "_Network");
		registerPacket(PacketSUseAbility.class, Side.SERVER);
		registerPacket(PacketSRequestData.class, Side.SERVER);
		registerPacket(PacketCPlayerData.class, Side.CLIENT);
		registerPacket(PacketSUseBendingController.class, Side.SERVER);
		
		BendingManager.init();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AvatarGuiHandler());
		
		FMLCommonHandler.instance().bus().register(new AvatarPlayerTick());
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
//		EntityRegistry.registerModEntity(EntityFloatingBlock.class, "FloatingBlock", 1, this, 256, 1, true);
		registerEntity(EntityFloatingBlock.class, "FloatingBlock");
		registerEntity(EntityFlame.class, "Flame");
		registerEntity(EntityFireArc.class, "FireArc");
		registerEntity(EntityWaterArc.class, "WaterArc");
		registerEntity(EntityAirGust.class, "AirGust");
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new AvatarCommand());
	}
	
	private <REQ extends IAvatarPacket<REQ>> void registerPacket(Class<REQ> packet, Side side) {
		network.registerMessage(packet, packet, nextMessageID++, side);
	}
	
	private void registerEntity(Class<? extends Entity> entity, String name) {
		EntityRegistry.registerGlobalEntityID(entity, name, EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(entity, name, nextEntityID++, this, 64, 20, true);
	}
	
}
