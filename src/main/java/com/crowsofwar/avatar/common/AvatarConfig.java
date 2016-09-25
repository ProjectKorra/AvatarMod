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
	
	public static ConfigurableProperty<Double> blockDamage, blockPush, ravinePush, wavePush;
	public static ConfigurableProperty<Integer> ravineDamage, waveDamage;
	
	public static void load() {
		
		try {
			config = Configuration.from("avatar/balance.cfg").withDefaults("config/balancedef.cfg");
			set(blockDamage, config.fromMapping("block").load("damageMultiplier").asDouble());
			set(ravineDamage, config.fromMapping("ravine").load("damage").asInt());
			set(waveDamage, config.fromMapping("wave").load("damage").asInt());
			
			set(blockPush, config.fromMapping("block").load("pushMultiplier").asDouble());
			set(ravinePush, config.fromMapping("ravine").load("pushMultiplier").asDouble());
			set(wavePush, config.fromMapping("wave").load("pushMultiplier").asDouble());
			
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Set the current value of that configurable property.
	 */
	public static <T> void set(ConfigurableProperty<T> key, T value) {
		key.value = value;
	}
	
	//@formatter:off
	public static void save() {
		try {
			config.fromMapping("block")
					.set("damageMultiplier",	blockDamage.currentValue())
					.set("pushMultiplier",		blockPush.currentValue());
			config.fromMapping("ravine")
					.set("damage",				ravineDamage.currentValue())
					.set("pushMultiplier",		ravinePush.currentValue());
			config.fromMapping("wave")
					.set("damage", 				waveDamage.currentValue())
					.set("pushMultiplier", 		wavePush.currentValue());
			
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Represents a Configuration entry. It has a String key and a changeable value.
	 * 
	 * @param <T>
	 *            The type of value
	 * 
	 * @author CrowsOfWar
	 */
	static class ConfigurableProperty<T> {
		
		private final String key;
		private T value;
		
		public ConfigurableProperty(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		public T currentValue() {
			return value;
		}
		
	}
	
}
