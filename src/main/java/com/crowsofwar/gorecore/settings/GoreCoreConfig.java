package com.crowsofwar.gorecore.settings;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class GoreCoreConfig {
	
	public GoreCoreConfig(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		loadValues(config);
		config.save();
	}
	
	protected abstract void loadValues(Configuration config);
	
}
