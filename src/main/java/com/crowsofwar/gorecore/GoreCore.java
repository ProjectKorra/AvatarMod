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

package com.crowsofwar.gorecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crowsofwar.gorecore.chat.ChatSender;
import com.crowsofwar.gorecore.config.convert.ConverterRegistry;
import com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy;
import com.crowsofwar.gorecore.settings.GoreCoreModConfig;
import com.crowsofwar.gorecore.tree.test.TreeTest;
import com.crowsofwar.gorecore.util.PlayerUUIDs;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = GoreCore.MOD_ID, name = GoreCore.MOD_NAME, version = GoreCore.MOD_VERSION)
public class GoreCore {
	
	public static final String MOD_ID = "GoreCore";
	public static final String MOD_NAME = "GoreCore";
	public static final String MOD_VERSION = "1.10-0.10.0";
	
	@SidedProxy(clientSide = "com.crowsofwar.gorecore.proxy.GoreCoreClientProxy", serverSide = "com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy")
	public static GoreCoreCommonProxy proxy;
	
	public static GoreCoreModConfig config;
	
	public static Logger LOGGER = LogManager.getLogger("GoreCore");
	
	public static final boolean IS_DEOBFUSCATED = Entity.class.getSimpleName().equals("Entity");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new GoreCoreModConfig(event);
		ConverterRegistry.addDefaultConverters();
		
		PlayerUUIDs.addUUIDsToCacheFromCacheFile();
		
		proxy.sideSpecifics();
		
		ChatSender.load();
		
	}
	
	@EventHandler
	public void onServerLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new TreeTest()); // TODO remove when testing
														// is over
	}
	
	// Called both on the client and on the dedicated server
	@EventHandler
	public void onShutdown(FMLServerStoppingEvent event) {
		PlayerUUIDs.saveUUIDCache();
	}
	
}
