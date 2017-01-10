/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.AvatarPlayerTick;
import com.crowsofwar.avatar.common.FallAbsorptionHandler;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.air.AirTick;
import com.crowsofwar.avatar.common.bending.earth.EarthSoundHandler;
import com.crowsofwar.avatar.common.bending.fire.FirebendingUpdate;
import com.crowsofwar.avatar.common.bending.water.WaterbendingUpdate;
import com.crowsofwar.avatar.common.command.AvatarCommand;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.entity.EntityWave;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import com.crowsofwar.avatar.common.network.packets.PacketCParticles;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;
import com.crowsofwar.avatar.common.network.packets.PacketSUseStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = AvatarInfo.MOD_ID, name = AvatarInfo.MOD_NAME, version = AvatarInfo.VERSION, dependencies = "required-after:gorecore", useMetadata = false, //
		updateJSON = "https://raw.githubusercontent.com/CrowsOfWar/AvatarMod/master/updates.json")

public class AvatarMod {
	
	@SidedProxy(serverSide = "com.crowsofwar.avatar.server.AvatarServerProxy", clientSide = "com.crowsofwar.avatar.client.AvatarClientProxy")
	public static AvatarCommonProxy proxy;
	
	@Instance(value = AvatarInfo.MOD_ID)
	public static AvatarMod instance;
	
	public static SimpleNetworkWrapper network;
	
	private int nextMessageID = 1;
	private int nextEntityID = 1;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		
		AvatarLog.log = e.getModLog();
		ConfigStats.load();
		ConfigSkills.load();
		
		BendingAbility.registerAbilities();
		BendingManager.init();
		
		EarthSoundHandler.register();
		
		AvatarParticles.register();
		AirTick.register();
		FallAbsorptionHandler.register();
		
		proxy.preInit();
		AvatarPlayerData.initFetcher(proxy.getClientDataFetcher());
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MOD_ID + "_Network");
		registerPacket(PacketSUseAbility.class, Side.SERVER);
		registerPacket(PacketSRequestData.class, Side.SERVER);
		registerPacket(PacketSUseBendingController.class, Side.SERVER);
		registerPacket(PacketSUseStatusControl.class, Side.SERVER);
		registerPacket(PacketCParticles.class, Side.CLIENT);
		registerPacket(PacketCPlayerData.class, Side.CLIENT);
		registerPacket(PacketSWallJump.class, Side.SERVER);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AvatarGuiHandler());
		
		FMLCommonHandler.instance().bus().register(new AvatarPlayerTick());
		
		AvatarDataSerializers.register();
		FloatingBlockBehavior.register();
		WaterArcBehavior.register();
		FireArcBehavior.register();
		WaterBubbleBehavior.register();
		WallBehavior.register();
		FireballBehavior.register();
		
		AvatarChatMessages.loadAll();
		
		MinecraftForge.EVENT_BUS.register(new FirebendingUpdate());
		WaterbendingUpdate.register();
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		registerEntity(EntityFloatingBlock.class, "FloatingBlock");
		registerEntity(EntityFireArc.class, "FireArc");
		registerEntity(EntityWaterArc.class, "WaterArc");
		registerEntity(EntityAirGust.class, "AirGust");
		registerEntity(EntityRavine.class, "Ravine");
		registerEntity(EntityFlames.class, "Flames");
		registerEntity(EntityWave.class, "Wave");
		registerEntity(EntityWaterBubble.class, "WaterBubble");
		registerEntity(EntityWall.class, "Wall");
		registerEntity(EntityWallSegment.class, "WallSegment");
		registerEntity(EntityFireball.class, "Fireball");
		registerEntity(EntityAirblade.class, "Airblade");
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new AvatarCommand());
	}
	
	private <MSG extends AvatarPacket<MSG>> void registerPacket(Class<MSG> packet, Side side) {
		network.registerMessage(packet, packet, nextMessageID++, side);
	}
	
	private void registerEntity(Class<? extends Entity> entity, String name) {
		EntityRegistry.registerModEntity(new ResourceLocation("avatarmod", name), entity, name,
				nextEntityID++, this, 64, 20, true);
	}
	
}
