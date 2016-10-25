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
	public static FloatingBlockSettings blockSettings = new FloatingBlockSettings();
	
	@Load
	public static RavineSettings ravineSettings = new RavineSettings();
	
	@Load
	public static WaveSettings waveSettings = new WaveSettings();
	
	static {
		
		allProperties = new HashMap<>();
		
	}
	
	public static void load() {
		
		ConfigLoader.load(AvatarConfig.class, "avatar/balance.cfg");
		
	}
	
	public static class FloatingBlockSettings {
		
		@Load
		public double damageMultiplier = 0.25;
		
		@Load
		public double pushMultiplier = 1;
		
	}
	
	public static class RavineSettings {
		
		@Load
		public int damage = 7;
		
		@Load
		public double pushMultiplier = 0.25;
		
	}
	
	public static class WaveSettings {
		
		@Load
		public int damage = 9;
		
		@Load
		public double pushMultiplier = 6;
		
	}
	
}
