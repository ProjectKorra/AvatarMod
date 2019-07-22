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

import com.crowsofwar.avatar.common.*;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.air.*;
import com.crowsofwar.avatar.common.bending.combustion.AbilityExplosion;
import com.crowsofwar.avatar.common.bending.combustion.AbilityExplosivePillar;
import com.crowsofwar.avatar.common.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.common.bending.earth.*;
import com.crowsofwar.avatar.common.bending.fire.*;
import com.crowsofwar.avatar.common.bending.ice.AbilityIceBurst;
import com.crowsofwar.avatar.common.bending.ice.AbilityIcePrison;
import com.crowsofwar.avatar.common.bending.ice.Icebending;
import com.crowsofwar.avatar.common.bending.lightning.*;
import com.crowsofwar.avatar.common.bending.sand.AbilitySandPrison;
import com.crowsofwar.avatar.common.bending.sand.AbilitySandstorm;
import com.crowsofwar.avatar.common.bending.sand.Sandbending;
import com.crowsofwar.avatar.common.bending.water.*;
import com.crowsofwar.avatar.common.blocks.AvatarBlocks;
import com.crowsofwar.avatar.common.command.AvatarCommand;
import com.crowsofwar.avatar.common.config.*;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.mob.EntityFirebender;
import com.crowsofwar.avatar.common.entity.mob.*;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.network.PacketHandlerServer;
import com.crowsofwar.avatar.common.network.packets.*;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.glider.api.upgrade.UpgradeItems;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.event.ServerEventHandler;
import com.crowsofwar.avatar.glider.common.network.PacketHandler;
import com.crowsofwar.avatar.glider.common.registry.CapabilityRegistry;
import com.crowsofwar.avatar.glider.common.wind.WindHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
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

import javax.annotation.Nonnull;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static net.minecraft.init.Biomes.*;
import static net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg;

@Mod(modid = AvatarInfo.MOD_ID, name = AvatarInfo.MOD_NAME, version = AvatarInfo.VERSION, dependencies = "required-after:gorecore", useMetadata = false, //
		updateJSON = "http://av2.io/updates.json", acceptedMinecraftVersions = "[1.12,1.13)")

public class AvatarMod {

	@SidedProxy(serverSide = "com.crowsofwar.avatar.server.AvatarServerProxy", clientSide = "com.crowsofwar.avatar.client.AvatarClientProxy")
	public static AvatarCommonProxy proxy;

	@Instance(value = AvatarInfo.MOD_ID)
	public static AvatarMod instance;

	public static SimpleNetworkWrapper network;

	public static boolean codeChickenLibCompat, realFirstPersonRender2Compat, cubicChunks;

	private int nextMessageID = 1;
	private int nextEntityID = 1;

	public static CreativeTabs tabItems = new CreativeTabs("avatar.items") {
		@Nonnull
		@Override
		public ItemStack createIcon() {
			return AvatarItems.stackScroll;
		}
	};

	private static void registerAbilities() {
		Abilities.register(new AbilityAirGust());
		Abilities.register(new AbilityAirJump());
		Abilities.register(new AbilityEarthControl());
		Abilities.register(new AbilityRavine());
		Abilities.register(new AbilityFireShot());
		Abilities.register(new AbilityFireBlast());
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
		Abilities.register(new AbilityImmolate());
		Abilities.register(new AbilityWaterCannon());
		Abilities.register(new AbilityFireJump());
		Abilities.register(new AbilityExplosion());
		Abilities.register(new AbilityExplosivePillar());
		Abilities.register(new AbilitySandstorm());
		Abilities.register(new AbilityInfernoPunch());
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

		codeChickenLibCompat = Loader.isModLoaded("codechickenlib");
		//Used for particle and inferno punch shenanigans
		realFirstPersonRender2Compat = Loader.isModLoaded("rfp2");
		//Prevents sky bison crashing
		cubicChunks = Loader.isModLoaded("cubicchunkscore");

		AvatarLog.log = e.getModLog();

		ConfigStats.load();
		ConfigSkills.load();
		ConfigClient.load();
		ConfigChi.load();
		ConfigMobs.load();
		ConfigAnalytics.load();//config
		ConfigHandler.init(e.getSuggestedConfigurationFile());

		//wind
		WindHelper.initNoiseGenerator();

		//register capabilities
		CapabilityRegistry.registerAllCapabilities();

		//packets
		PacketHandler.init();

		AvatarControl.initControls();
		registerAbilities();
		registerBendingStyles();
		AvatarItems.init();
		AvatarBlocks.init();
		AvatarParticles.register();

		proxy.preInit();
		AvatarPlayerData.initFetcher(proxy.getClientDataFetcher());

		network = NetworkRegistry.INSTANCE.newSimpleChannel(AvatarInfo.MOD_ID + "_Network");
		registerPacket(PacketSUseAbility.class, Side.SERVER);
		registerPacket(PacketSRequestData.class, Side.SERVER);
		registerPacket(PacketSUseStatusControl.class, Side.SERVER);
		registerPacket(PacketCParticles.class, Side.CLIENT);
		registerPacket(PacketCPlayerData.class, Side.CLIENT);
		registerPacket(PacketSWallJump.class, Side.SERVER);
		registerPacket(PacketSSkillsMenu.class, Side.SERVER);
		registerPacket(PacketSUseScroll.class, Side.SERVER);
		registerPacket(PacketCErrorMessage.class, Side.CLIENT);
		registerPacket(PacketSBisonInventory.class, Side.SERVER);
		registerPacket(PacketSOpenUnlockGui.class, Side.SERVER);
		registerPacket(PacketSUnlockBending.class, Side.SERVER);
		registerPacket(PacketSConfirmTransfer.class, Side.SERVER);
		registerPacket(PacketSCycleBending.class, Side.SERVER);
		registerPacket(PacketCPowerRating.class, Side.CLIENT);
		registerPacket(PacketCOpenSkillCard.class, Side.CLIENT);
		registerPacket(PacketSSendViewStatus.class, Side.SERVER);
		registerPacket(PacketSParticleCollideEvent.class, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AvatarGuiHandler());

		FMLCommonHandler.instance().bus().register(new AvatarPlayerTick());

		AvatarDataSerializers.register();

		AvatarChatMessages.loadAll();

		Behavior.registerBehaviours();

		EarthbendingEvents.register();

		PacketHandlerServer.register();

		HumanBenderSpawner.register();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, (tickets, world) -> {
		});

		AvatarAnnouncements.fetchAnnouncements();

	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		//Anything with a low update frequency needs that frequency in order to function properly. Otherwise, they have a high frequency (ticks between updates).
		registerEntity(EntityFloatingBlock.class, "FloatingBlock", 128, 1000, true);
		registerEntity(EntityFireArc.class, "FireArc", 128, 1000, true);
		registerEntity(EntityWaterArc.class, "WaterArc", 128, 1000, true);
		registerEntity(EntityAirGust.class, "AirGust", 128, 1000, true);
		registerEntity(EntityRavine.class, "Ravine", 128, 1000, true);
		//For some reason, there's random desync for its position when spawning it. Setting it to 1 fixes it.
		registerEntity(EntityFlames.class, "Flames", 128, 1000, true);
		registerEntity(EntityWave.class, "Wave", 128, 1000, true);
		registerEntity(EntityWaterBubble.class, "WaterBubble", 128, 1000, true);
		registerEntity(EntityWall.class, "Wall", 128, 3, true);
		registerEntity(EntityWallSegment.class, "WallSegment", 128, 3, true);
		registerEntity(EntityFireball.class, "Fireball", 128, 1000, true);
		registerEntity(EntityAirblade.class, "Airblade", 128, 1000, true);
		registerEntity(EntityAirBubble.class, "AirBubble", 128, 1000, false);
		registerEntity(EntityFirebender.class, "Firebender", 0xB0171F, 0xFFFF00);
		registerEntity(EntityAirbender.class, "Airbender", 0xffffff, 0xDDA0DD);
		registerEntity(EntitySkyBison.class, "SkyBison", 0xffffff, 0x8B5A00);
		registerEntity(EntityOtterPenguin.class, "OtterPenguin", 0xffffff, 0x104E8B);
		registerEntity(AvatarEntityItem.class, "Item", 128, 3, true);
		registerEntity(EntityIceShield.class, "iceshield", 128, 1000, true);
		registerEntity(EntityIceShard.class, "iceshard", 128, 1000, true);
		registerEntity(EntityIcePrison.class, "iceprison", 128, 1000, true);
		registerEntity(EntityOstrichHorse.class, "OstrichHorse", 0x5c5b46, 0x0f1108);
		registerEntity(EntitySandPrison.class, "sandprison", 128, 1000, true);
		registerEntity(EntityLightningArc.class, "Lightning_arc", 128, 1000, true);
		registerEntity(EntityCloudBall.class, "Cloudburst", 128, 1000, true);
		registerEntity(EntityEarthspike.class, "Earthspike", 128, 1000, false);
		registerEntity(EntityLightningSpear.class, "Lightning_Spear", 128, 1000, true);
		registerEntity(EntityEarthspikeSpawner.class, "EarthspikeSpawner", 128, 1000, true);
		registerEntity(EntityWaterCannon.class, "WaterCannon", 128, 1, true);
		registerEntity(EntitySandstorm.class, "Sandstorm", 128, 4, true);
		registerEntity(EntityExplosionSpawner.class, "ExplosionSpawner", 128, 1000, true);
		registerEntity(EntityLightningSpawner.class, "LightningSpawner", 128, 1000, true);
		registerEntity(EntityShockwave.class, "Shockwave", 128, 1000, false);
		registerEntity(EntityLightOrb.class, "LightOrb", 128, 1000, true);
		registerEntity(EntityLightCylinder.class, "LightCylinder", 128, 1000, true);
		registerEntity(EntityFlamethrower.class, "Flamethrower", 128, 1000, true);

		EntityRegistry.addSpawn(EntitySkyBison.class, 5, 1, 3, EnumCreatureType.CREATURE, //
				SAVANNA_PLATEAU, EXTREME_HILLS, BIRCH_FOREST_HILLS, TAIGA_HILLS, ICE_MOUNTAINS, REDWOOD_TAIGA_HILLS, MUTATED_EXTREME_HILLS,
				MUTATED_EXTREME_HILLS_WITH_TREES, EXTREME_HILLS_WITH_TREES, EXTREME_HILLS_EDGE);
		EntityRegistry.addSpawn(EntityOtterPenguin.class, 10, 3, 6, EnumCreatureType.CREATURE, //
				COLD_BEACH, ICE_PLAINS, ICE_MOUNTAINS, MUTATED_ICE_FLATS);
		EntityRegistry.addSpawn(EntityOstrichHorse.class, 5, 1, 3, EnumCreatureType.CREATURE, //
				DESERT, DESERT_HILLS, SAVANNA, SAVANNA_PLATEAU, PLAINS);

		// Second loading required since other mods blocks and items might not be
		// registered
		STATS_CONFIG.loadBlocks();
		MOBS_CONFIG.loadLists();
		ConfigMobs.load();

		//glider upgrades
		UpgradeItems.initUpgradesList();

		//register server events
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
		//register config changed event
		MinecraftForge.EVENT_BUS.register(new ConfigHandler());

		proxy.init();
		proxy.registerParticles();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		AvatarAnalytics.INSTANCE.init();
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new AvatarCommand());
	}

	private <MSG extends AvatarPacket<MSG>> void registerPacket(Class<MSG> packet, Side side) {
		network.registerMessage(packet, packet, nextMessageID++, side);
	}

	private void registerEntity(Class<? extends Entity> entity, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(new ResourceLocation("avatarmod", name), entity, name,
				nextEntityID++, this, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	private void registerEntity(Class<? extends Entity> entity, String name, int primary, int secondary) {
		registerEntity(entity, name, 128, 3, true);
		registerEgg(new ResourceLocation("avatarmod", name), primary, secondary);
	}

}
