package com.crowsofwar.avatar.common;

import com.crowsofwar.gorecore.config.Configuration;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	private static int floatingBlockDamage, ravineDamage, waveDamage;
	private static double blockPush, ravinePush, wavePush;
	
	public static void load() {
		
		Configuration config = Configuration.from("avatar/balance.cfg").withDefaults("config/balancedef.cfg");
		floatingBlockDamage = config.fromMapping("block").load("damageMultiplier").asInt();
		ravineDamage = config.fromMapping("ravine").load("damage").asInt();
		waveDamage = config.fromMapping("wave").load("damage").asInt();
		
		blockPush = config.fromMapping("block").load("pushMultiplier").asDouble();
		ravinePush = config.fromMapping("ravine").load("pushMultiplier").asDouble();
		wavePush = config.fromMapping("wave").load("pushMultiplier").asDouble();
		
	}
	
	public static int getFloatingBlockDamage() {
		return floatingBlockDamage;
	}
	
	public static int getRavineDamage() {
		return ravineDamage;
	}
	
	public static int getWaveDamage() {
		return waveDamage;
	}
	
	public static double getRavinePush() {
		return ravinePush;
	}
	
	public static double getWavePush() {
		return wavePush;
	}
	
	public static double getBlockPush() {
		return blockPush;
	}
	
}
