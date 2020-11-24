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

import com.crowsofwar.gorecore.config.convert.ConverterRegistry;
import com.crowsofwar.gorecore.format.ChatSender;
import com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy;
import com.crowsofwar.gorecore.settings.GoreCoreModConfig;
import com.crowsofwar.gorecore.tree.test.GoreCoreChatMessages;
import com.crowsofwar.gorecore.tree.test.GoreCoreCommand;
import com.crowsofwar.gorecore.tree.test.TreeTest;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

@Mod(modid = GoreCore.MOD_ID, name = GoreCore.MOD_NAME, version = GoreCore.MOD_VERSION)
public class GoreCore {

	public static final String MOD_ID = "gorecore";
	public static final String MOD_NAME = "GoreCore";
	public static final String MOD_VERSION = "1.12.2-0.4.5";

	@SidedProxy(clientSide = "com.crowsofwar.gorecore.proxy.GoreCoreClientProxy", serverSide = "com.crowsofwar.gorecore.proxy.GoreCoreCommonProxy")
	public static GoreCoreCommonProxy proxy;

	public static GoreCoreModConfig config;

	public static Logger LOGGER = LogManager.getLogger("GoreCore");

	public static final boolean IS_DEOBFUSCATED = Entity.class.getSimpleName().equals("Entity");

	//Simple boolean, so that Loader.isLoaded() doesn't have to be called each time
	public static boolean spongeCompat;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new GoreCoreModConfig(event);
		ConverterRegistry.addDefaultConverters();

		File oldFile = GoreCore.proxy.getUUIDCacheFile();
		if (oldFile.exists()) {
			// We don't need a cache anymore
			oldFile.delete();
		}
		GoreCoreChatMessages.register();

		proxy.sideSpecifics();

		ChatSender.load();

		spongeCompat = Loader.isModLoaded("sponge");
	}

	@EventHandler
	public void onServerLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new TreeTest()); // TODO remove when testing
														// is over
		event.registerServerCommand(new GoreCoreCommand());
	}

}
