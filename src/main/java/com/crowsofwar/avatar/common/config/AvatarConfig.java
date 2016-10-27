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
	
	public AttackSettings getFloatingBlockSettings() {
		return floatingBlockSettings;
	}
	
	public AttackSettings getRavineSettings() {
		return ravineSettings;
	}
	
	public AttackSettings getWaveSettings() {
		return waveSettings;
	}
	
	public static void load() {
		
		ConfigLoader.load(AvatarConfig.class, "avatar/balance.cfg");
		
	}
	
	public static class AttackSettings {
		
		@Load
		private double damageMultiplier;
		
		@Load
		private double pushMultiplier;
		
		public AttackSettings() {}
		
		public AttackSettings(double damage, double push) {
			this.damageMultiplier = damage;
			this.pushMultiplier = push;
		}
		
		/**
		 * Get the damage. For floating blocks, this is the damage multiplier
		 * actually.
		 */
		public double getDamage() {
			return damageMultiplier;
		}
		
		public double getPush() {
			return pushMultiplier;
		}
		
	}
	
}
