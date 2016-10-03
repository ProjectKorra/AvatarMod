package com.crowsofwar.avatar.common.config;

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	static final Map<String, ConfigurableProperty<?>> allProperties;
	
	@Load
	public static FloatingBlockSettings blockSettings;
	
	@Load
	public static RavineSettings ravineSettings;
	
	@Load
	public static WaveSettings waveSettings;
	
	static {
		
		allProperties = new HashMap<>();
		
	}
	
	public static void load() {
		
		ConfigLoader.load(AvatarConfig.class, "avatar/balance.cfg");
		
	}
	
	public static class FloatingBlockSettings {
		
		@Load
		public static double damageMultiplier;
		
		@Load
		public static double pushMultiplier;
		
	}
	
	public static class RavineSettings {
		
		@Load
		public static int damage;
		
		@Load
		public static double pushMultiplier;
		
	}
	
	public static class WaveSettings {
		
		@Load
		public static int damage;
		
		@Load
		public static double pushMultiplier;
		
	}
	
}
