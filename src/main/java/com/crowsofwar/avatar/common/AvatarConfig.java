package com.crowsofwar.avatar.common;

import com.crowsofwar.gorecore.config.Configuration;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	private static int FLOATING_BLOCK_DAMAGE, RAVINE_DAMAGE, WAVE_DAMAGE;
	private static double RAVINE_PUSH_MULTIPLIER, WAVE_PUSH_MULTIPLIER;
	
	public static void load() {
		
		Configuration config = Configuration.from("avatar/balance.cfg").withDefaults("config/balancedef.cfg");
		FLOATING_BLOCK_DAMAGE = config.fromMapping("block").load("floatingBlockDamage").asInt();
		RAVINE_DAMAGE = config.fromMapping("ravine").load("ravineDamage").asInt();
		WAVE_DAMAGE = config.fromMapping("wave").load("waveDamage").asInt();
		
		RAVINE_PUSH_MULTIPLIER = config.fromMapping("ravine").load("ravinePush").asDouble();
		WAVE_PUSH_MULTIPLIER = config.fromMapping("wave").load("wavePush").asDouble();
		
	}
	
}
