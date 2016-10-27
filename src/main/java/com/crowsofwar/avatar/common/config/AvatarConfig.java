package com.crowsofwar.avatar.common.config;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	@Load
	private AttackSettings floatingBlockSettings = new AttackSettings(0.25, 1),
			ravineSettings = new AttackSettings(7, 0.25), //
			waveSettings = new AttackSettings(9, 6);
	
	public static void load() {
		
		ConfigLoader.load(AvatarConfig.class, "avatar/balance.cfg");
		
	}
	
	public static class AttackSettings {
		
		@Load
		private double damageMultiplier = 0.25;
		
		@Load
		private double pushMultiplier = 1;
		
		public AttackSettings() {}
		
		public AttackSettings(double damage, double push) {
			this.damageMultiplier = damage;
			this.pushMultiplier = push;
		}
		
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
