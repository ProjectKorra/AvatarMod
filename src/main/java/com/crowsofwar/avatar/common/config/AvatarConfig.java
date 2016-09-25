package com.crowsofwar.avatar.common.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.gorecore.config.Configuration;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	private static Configuration config;
	static final Map<String, ConfigurableProperty<Object>> allProperties;
	
	public static final ConfigurableProperty<Double> blockDamage, blockPush, ravinePush, wavePush;
	public static final ConfigurableProperty<Integer> ravineDamage, waveDamage;
	
	static {
		
		allProperties = new HashMap<>();
		
		blockPush = new ConfigurablePropertyDouble("block.pushMultiplier");
		blockDamage = new ConfigurablePropertyDouble("block.damageMultiplier");
		
		ravinePush = new ConfigurablePropertyDouble("ravine.pushMultiplier");
		ravineDamage = new ConfigurableProperty<>("ravine.damage");
		
		wavePush = new ConfigurablePropertyDouble("wave.pushMultiplier");
		waveDamage = new ConfigurableProperty<>("wave.damage");
		
	}
	
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
		key.setValue(value);
	}
	
	public static void set(String key, Object value) {
		set(allProperties.get(key), value);
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
	
}
