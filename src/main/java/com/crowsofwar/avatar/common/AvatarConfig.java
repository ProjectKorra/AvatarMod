package com.crowsofwar.avatar.common;

import java.io.IOException;

import com.crowsofwar.gorecore.config.Configuration;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	private static Configuration config;
	
	public static ConfigurableValue<Double> blockDamage, blockPush, ravinePush, wavePush;
	public static ConfigurableValue<Integer> ravineDamage, waveDamage;
	
	// public static double blockDamage;
	// public static int ravineDamage, waveDamage;
	// public static double blockPush, ravinePush, wavePush;
	
	public static void load() {
		
		try {
			config = Configuration.from("avatar/balance.cfg").withDefaults("config/balancedef.cfg");
			blockDamage = config.fromMapping("block").load("damageMultiplier").asDouble();
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
	
	public static void set(String key, Object value) {
		
	}
	
	public static void save() {
		try {
			config.fromMapping("block").set("damageMultiplier", blockDamage).set("pushMultiplier", blockPush);
			config.fromMapping("ravine").set("damage", ravineDamage).set("pushMultiplier", ravinePush);
			config.fromMapping("wave").set("damage", waveDamage).set("pushMultiplier", wavePush);
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class ConfigurableValue<T> {
		
		private String key;
		private T value;
		
		public ConfigurableValue(String key) {
			super();
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}
		
		public T getValue() {
			return value;
		}
		
		public void setValue(T value) {
			this.value = value;
		}
		
	}
	
}
