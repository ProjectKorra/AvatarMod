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

import net.minecraft.entity.*;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.crowsofwar.avatar.common.*;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.bending.air.*;
import com.crowsofwar.avatar.common.bending.combustion.*;
import com.crowsofwar.avatar.common.bending.earth.*;
import com.crowsofwar.avatar.common.bending.fire.*;
import com.crowsofwar.avatar.common.bending.ice.*;
import com.crowsofwar.avatar.common.bending.lightning.*;
import com.crowsofwar.avatar.common.bending.sand.*;
import com.crowsofwar.avatar.common.bending.water.*;
import com.crowsofwar.avatar.common.command.AvatarCommand;
import com.crowsofwar.avatar.common.config.*;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.data.*;
import com.crowsofwar.avatar.common.entity.mob.*;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.network.packets.*;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;

import static com.crowsofwar.avatar.AvatarInfo.*;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static net.minecraft.init.Biomes.*;
import static net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:gorecore", useMetadata = true, acceptedMinecraftVersions = MC_VERSION)

public class AvatarMod {
	@SidedProxy(serverSide = "com.crowsofwar.avatar.server.AvatarServerProxy", clientSide = "com.crowsofwar.avatar.client.AvatarClientProxy")
	public static AvatarCommonProxy proxy;

	@Instance(MODID)
	public static AvatarMod instance;

	public static SimpleNetworkWrapper network;

	private int nextMessageID = 1;
	private int nextEntityID = 1;

	private static void registerAbilities() {
		Abilities.register(new AbilityAirGust());
		Abilities.register(new AbilityAirJump());
		Abilities.register(new AbilityPickUpBlock());
		Abilities.register(new AbilityRavine());
		Abilities.register(new AbilityLightFire());
		Abilities.register(new AbilityFireArc());
		Abilities.register(new AbilityFlamethrower());
		Abilities.register(new AbilityWaterArc());
		Abilities.register(new AbilityCreateWave());
		Abilities.register(new AbilityWaterBubble());
		Abilities.register(new AbilityWall());
		Abilities.register(new AbilityWaterSkate());
		Abilities.register(new AbilityFireball());
		Abilities.register(new AbilityAirblade());
		Abilities.register(new AbilityMining());
		Abilities.register(new AbilityAirBubble());
		Abilities.register(new AbilityIceBurst());
		Abilities.register(new AbilityIcePrison());
		Abilities.register(new AbilitySandPrison());
		Abilities.register(new AbilityLightningArc());
		Abilities.register(new AbilityLightningRedirect());
		Abilities.register(new AbilityCloudBurst());
		Abilities.register(new AbilityRestore());
		Abilities.register(new AbilitySlipstream());
		Abilities.register(new AbilityCleanse());
		Abilities.register(new AbilityEarthspikes());
		Abilities.register(new AbilityLightningSpear());
		Abilities.register(new AbilityPurify());
		Abilities.register(new AbilityWaterCannon());
		Abilities.register(new AbilityFireJump());
		Abilities.register(new AbilityExplosion());
		Abilities.register(new AbilityExplosivePillar());
		Abilities.register(new AbilitySandstorm());
		Abilities.register(new AbilityInfernoPunch());
		Abilities.register(new AbilityBoulderRing());
		Abilities.register(new AbilityLightningRaze());
		Abilities.register(new AbilityAirBurst());
	}

	private static void registerBendingStyles() {
		BendingStyles.register(new Earthbending());
		BendingStyles.register(new Firebending());
		BendingStyles.register(new Waterbending());
		BendingStyles.register(new Airbending());
		BendingStyles.register(new Icebending());
		BendingStyles.register(new Lightningbending());
		BendingStyles.register(new Sandbending());
		BendingStyles.register(new Combustionbending());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {

		AvatarLog.log = e.getModLog();

		ConfigStats.load();
		ConfigSkills.load();
		ConfigClient.load();
		ConfigChi.load();
		ConfigMobs.load();
		ConfigAnalytics.load();

		AvatarControl.initControls();
		registerAbilities();
		registerBendingStyles();
		AvatarItems.init();
		AvatarParticles.register();

		proxy.preInit();
		AvatarPlayerData.initFetcher(proxy.getClientDataFetcher());

		network = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MODID + "_Network");
		network.registerMessage(PacketSUseAbility.Handler.class, PacketSUseAbility.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSRequestData.Handler.class, PacketSRequestData.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSUseStatusControl.Handler.class, PacketSUseStatusControl.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketCParticles.Handler.class, PacketCParticles.class, nextMessageID++, Side.CLIENT);
		network.registerMessage(PacketCPlayerData.Handler.class, PacketCPlayerData.class, nextMessageID++, Side.CLIENT);
		network.registerMessage(PacketSWallJump.Handler.class, PacketSWallJump.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSSkillsMenu.Handler.class, PacketSSkillsMenu.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSUseScroll.Handler.class, PacketSUseScroll.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketCErrorMessage.Handler.class, PacketCErrorMessage.class, nextMessageID++, Side.CLIENT);
		network.registerMessage(PacketSBisonInventory.Handler.class, PacketSBisonInventory.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSOpenUnlockGui.Handler.class, PacketSOpenUnlockGui.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSUnlockBending.Handler.class, PacketSUnlockBending.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSConfirmTransfer.Handler.class, PacketSConfirmTransfer.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketSCycleBending.Handler.class, PacketSCycleBending.class, nextMessageID++, Side.SERVER);
		network.registerMessage(PacketCPowerRating.Handler.class, PacketCPowerRating.class, nextMessageID++, Side.CLIENT);
		network.registerMessage(PacketCOpenSkillCard.Handler.class, PacketCOpenSkillCard.class, nextMessageID++, Side.CLIENT);
		AvatarLog.info("Registered " + nextMessageID + " packet types");

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AvatarGuiHandler());

		FMLCommonHandler.instance().bus().register(new AvatarPlayerTick());

		AvatarDataSerializers.register();
		FloatingBlockBehavior.register();
		WaterArcBehavior.register();
		FireArcBehavior.register();
		WaterBubbleBehavior.register();
		WallBehavior.register();
		FireballBehavior.register();
		CloudburstBehavior.register();
		AvatarChatMessages.loadAll();
		LightningSpearBehavior.register();
		BoulderBehavior.register();

		EarthbendingEvents.register();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, (tickets, world) -> {
		});

		AvatarAnnouncements.fetchAnnouncements();

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
		registerEntity(EntityAirBubble.class, "AirBubble");
		registerEntity(EntityFirebender.class, "Firebender", 0xffffff, 0xffffff);
		registerEntity(EntityAirbender.class, "Airbender", 0xffffff, 0xffffff);
		registerEntity(EntitySkyBison.class, "SkyBison", 0xffffff, 0xffffff);
		registerEntity(EntityOtterPenguin.class, "OtterPenguin", 0xffffff, 0xffffff);
		registerEntity(AvatarEntityItem.class, "Item");
		registerEntity(EntityIceShield.class, "iceshield");
		registerEntity(EntityIceShard.class, "iceshard");
		registerEntity(EntityIcePrison.class, "iceprison");
		registerEntity(EntityOstrichHorse.class, "OstrichHorse", 0x5c5b46, 0x0f1108);
		registerEntity(EntitySandPrison.class, "sandprison");
		registerEntity(EntityLightningArc.class, "Lightning_arc");
		registerEntity(EntityCloudBall.class, "Cloudburst");
		registerEntity(EntityEarthspike.class, "Earthspike");
		registerEntity(EntityLightningSpear.class, "Lightning_Spear");
		registerEntity(EntityEarthspikeSpawner.class, "EarthspikeSpawner");
		registerEntity(EntityWaterCannon.class, "WaterCannon");
		registerEntity(EntitySandstorm.class, "Sandstorm");
		registerEntity(EntityExplosionSpawner.class, "ExplosionSpawner");
		registerEntity(EntityBoulder.class, "Boulder");
		registerEntity(EntityLightningSpawner.class, "LightningSpawnerr");

		EntityRegistry.addSpawn(EntitySkyBison.class, 5, 3, 6, EnumCreatureType.CREATURE, //
								EXTREME_HILLS, MUTATED_SAVANNA);
		EntityRegistry.addSpawn(EntityOtterPenguin.class, 4, 5, 9, EnumCreatureType.CREATURE, //
								COLD_BEACH, ICE_PLAINS, ICE_MOUNTAINS, MUTATED_ICE_FLATS);
		EntityRegistry.addSpawn(EntityOstrichHorse.class, 5, 3, 6, EnumCreatureType.CREATURE, //
								DESERT, DESERT_HILLS, SAVANNA, SAVANNA_PLATEAU, PLAINS);

		// Second loading required since other mods blocks might not be
		// registered
		STATS_CONFIG.loadBlocks();

		proxy.init();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		AvatarAnalytics.INSTANCE.init();
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new AvatarCommand());
	}

	private void registerEntity(Class<? extends Entity> entity, String name) {
		EntityRegistry.registerModEntity(new ResourceLocation("avatarmod", name), entity, name, nextEntityID++, this, 128, 3, true);
	}

	private void registerEntity(Class<? extends Entity> entity, String name, int primary, int secondary) {
		registerEntity(entity, name);
		registerEgg(new ResourceLocation("avatarmod", name), primary, secondary);
	}

}
