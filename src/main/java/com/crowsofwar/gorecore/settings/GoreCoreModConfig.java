package com.crowsofwar.gorecore.settings;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class GoreCoreModConfig extends GoreCoreConfig {
	
	public int MAX_UUID_CACHE_SIZE;
	
	public GoreCoreModConfig(FMLPreInitializationEvent event) {
		super(event);
	}
	
	@Override
	protected void loadValues(Configuration config) {
		MAX_UUID_CACHE_SIZE = config.getInt("Max UUID Cache Size", "misc", 200, 5, 100000,
				"The maximum amount of UUIDs that can be stored in the UUID cache file");
	}
	
}
