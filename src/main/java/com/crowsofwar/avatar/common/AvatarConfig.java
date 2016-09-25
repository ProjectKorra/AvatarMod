package com.crowsofwar.avatar.common;

import java.io.IOException;

import com.crowsofwar.gorecore.config.Configuration;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	public static double floatingBlockDamage;
	public static int ravineDamage, waveDamage;
	public static double blockPush, ravinePush, wavePush;
	
	public static void load() {
		
		try {
			Configuration config = Configuration.from("avatar/balance.cfg")
					.withDefaults("config/balancedef.cfg");
			floatingBlockDamage = config.fromMapping("block").load("damageMultiplier").asDouble();
			ravineDamage = config.fromMapping("ravine").load("damage").asInt();
			waveDamage = config.fromMapping("wave").load("damage").asInt();
			
			blockPush = config.fromMapping("block").load("pushMultiplier").asDouble();
			ravinePush = config.fromMapping("ravine").load("pushMultiplier").asDouble();
			wavePush = config.fromMapping("wave").load("pushMultiplier").asDouble();
			
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
