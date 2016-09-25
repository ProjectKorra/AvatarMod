package com.crowsofwar.avatar.common.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigurablePropertyDouble extends ConfigurableProperty<Double> {
	
	/**
	 * @param key
	 */
	public ConfigurablePropertyDouble(String key) {
		super(key);
	}
	
	@Override
	void setValue(Double value) {
		super.setValue(Double.valueOf(value + ""));
	}
	
}
