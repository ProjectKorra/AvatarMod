package com.crowsofwar.avatar;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarPlayerTick;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.command.AvatarCommand;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlame;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.IAvatarPacket;
import com.crowsofwar.avatar.common.network.packets.PacketCControlPoints;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;
import com.google.common.base.Function;

import cpw.mods.fml.client.FMLClientHandler;
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
import cpw.mods.fml.common.network.internal.CrowsOfWar_PacketHack;
import cpw.mods.fml.common.network.internal.EntitySpawnHandler;
import cpw.mods.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

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
		registerPacket(PacketCControlPoints.class, Side.CLIENT);
		
		BendingManager.init();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AvatarGuiHandler());
		
		FMLCommonHandler.instance().bus().register(new AvatarPlayerTick());
		
	}
	
	private <T extends Entity> T makeEntity(EntitySpawnMessage msg, Class<T> cls) {
		try {
			WorldClient wc = FMLClientHandler.instance().getWorldClient();
			
			T entity = (cls.getConstructor(World.class).newInstance(wc));
			
			final int entityId = CrowsOfWar_PacketHack.getEntityId(msg);
			final double scaledX = CrowsOfWar_PacketHack.getScaledX(msg);
			final double scaledY = CrowsOfWar_PacketHack.getScaledY(msg);
			final double scaledZ = CrowsOfWar_PacketHack.getScaledZ(msg);
			final float scaledPitch = CrowsOfWar_PacketHack.getScaledPitch(msg);
			final float scaledHeadYaw = CrowsOfWar_PacketHack.getScaledHeadYaw(msg);
			final float scaledYaw = CrowsOfWar_PacketHack.getScaledYaw(msg);
			
	        int offset = entityId - entity.getEntityId();
	        entity.setEntityId(entityId);
	        entity.setLocationAndAngles(scaledX, scaledY, scaledZ, scaledYaw, scaledPitch);
	        if (entity instanceof EntityLiving)
	        {
	            ((EntityLiving) entity).rotationYawHead = scaledHeadYaw;
	        }

	        Entity parts[] = entity.getParts();
	        if (parts != null)
	        {
	            for (int j = 0; j < parts.length; j++)
	            {
	                parts[j].setEntityId(parts[j].getEntityId() + offset);
	            }
	        }
	        
            return entity;
	        
		} catch (Exception e) {
			System.out.println("Couldn't spawn entity into world");
			e.printStackTrace();
			return null;
		}

		
	}
//	
//	private void addToWorld(Entity entity, EntitySpawnMessage msg) {
//		entity.serverPosX = CrowsOfWar_PacketHack.getRawX(msg);
//        entity.serverPosY = CrowsOfWar_PacketHack.getRawY(msg);
//        entity.serverPosZ = CrowsOfWar_PacketHack.getRawZ(msg);
//
//        final int throwerId = CrowsOfWar_PacketHack.getThrowerId(msg);
//        
//        EntityClientPlayerMP clientPlayer = FMLClientHandler.instance().getClientPlayerEntity();
//
//        if (entity instanceof IEntityAdditionalSpawnData)
//        {
//            ((IEntityAdditionalSpawnData) entity).readSpawnData(CrowsOfWar_PacketHack.getDataStream(msg));
//        }
//        wc.addEntityToWorld(entityId, entity);
//	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
//		EntityRegistry.registerModEntity(EntityFloatingBlock.class, "FloatingBlock", 1, this, 256, 1, true);
		registerEntity(EntityFloatingBlock.class, "FloatingBlock");
		registerEntity(EntityFlame.class, "Flame");
		
		// Fire spawn function: Create Control Points
		Function<EntitySpawnMessage, Entity> fireSpawnFunc = (spawnMsg) -> {
			System.out.println("Fire spawn func");
//			
//			EntityFireArc arc = doSpawn(spawnMsg, EntityFireArc.class, false);
//			
//			EntityControlPoint[] points = new EntityControlPoint[arc.getAmountOfControlPoints()];
//			for (int i = 0; i < points.length; i++) {
//				points[i] = arc.createControlPoint(0.2f);
//				
//			}
//			
			
			return makeEntity(spawnMsg, EntityFireArc.class);
		};
		// Fire CP spawn function: Do nothing. Fire spawn func creates cps.
		Function<EntitySpawnMessage, Entity> fireCpSpawnFunc = (spawnMsg) -> {
			System.out.println("Cp spawn func");
			return null;
		};
		
		
		registerEntity(EntityFireArc.class, "FireArc");
		registerEntity(EntityFireArc.FireControlPoint.class, "FireArcCP");
		
		
		
		registerEntity(EntityWaterArc.class, "WaterArc");
		registerEntity(EntityWaterArc.WaterControlPoint.class, "WaterArcCP");
		registerEntity(EntityAirGust.class, "AirGust");
		registerEntity(EntityAirGust.AirGustControlPoint.class, "AirGustCP");
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
	
	private void registerEntity(Class<? extends Entity> entity, String name, Function<EntitySpawnMessage, Entity> customSpawn, boolean useVanillaSpawning) {
		registerEntity(entity, name);
		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity, false);
		er.setCustomSpawning(customSpawn, useVanillaSpawning);
	}
	
}
