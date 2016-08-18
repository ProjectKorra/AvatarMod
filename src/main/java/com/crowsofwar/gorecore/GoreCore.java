package com.crowsofwar.gorecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy;
import com.crowsofwar.gorecore.settings.GoreCoreModConfig;
import com.crowsofwar.gorecore.tree.test.TreeTest;
import com.crowsofwar.gorecore.util.GoreCoreIsPlayerWalking;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import com.crowsofwar.gorecore.util.GoreCoreVersionCheckerServerChat;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = GoreCore.MOD_ID, name = GoreCore.MOD_NAME, version = GoreCore.MOD_VERSION)
public class GoreCore {
	
	public static final String MOD_ID = "GoreCore";
	public static final String MOD_NAME = "GoreCore";
	public static final String MOD_VERSION = "1.7.10-0.9.0";
	
	@SidedProxy(clientSide = "com.crowsofwar.gorecore.proxy.GoreCoreClientProxy", serverSide = "com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy")
	public static GoreCoreCommonProxy proxy;
	
	public static GoreCoreModConfig config;
	
	public static Logger LOGGER = LogManager.getLogger("GoreCore");
	
	/**
	 * The "is player walking" detector for the client-side.
	 */
	@SideOnly(Side.CLIENT)
	public static GoreCoreIsPlayerWalking walkDetectorClient;
	
	/**
	 * The "is player walking" detector for dedicated or integrated servers.
	 */
	public static GoreCoreIsPlayerWalking walkDetectorServer;
	
	public static final boolean IS_DEOBFUSCATED = Entity.class.getSimpleName().equals("Entity");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new GoreCoreModConfig(event);
		
		GoreCorePlayerUUIDs.addUUIDsToCacheFromCacheFile();
		walkDetectorClient = proxy.initPlayerWalkingClient();
		walkDetectorServer = new GoreCoreIsPlayerWalking();
		
		new GoreCoreVersionCheckerServerChat("gc.message.versionCheck", MOD_VERSION,
				"https://raw.githubusercontent.com/CrowsOfWar/PhysicalTraits/master/fetch/latest-version-gc.txt");
		
		proxy.sideSpecifics();
		
	}
	
	@EventHandler
	public void onServerLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new TreeTest()); // TODO remove when testing is over
	}
	
	// Called both on the client and on the dedicated server
	@EventHandler
	public void onShutdown(FMLServerStoppingEvent event) {
		GoreCorePlayerUUIDs.saveUUIDCache();
	}
	
}
