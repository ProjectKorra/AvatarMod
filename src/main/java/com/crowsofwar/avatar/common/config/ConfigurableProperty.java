package com.crowsofwar.avatar.common.config;

/**
 * Represents a Configuration entry. It has a String key and a changeable value.
 * 
 * @param <T>
 *            The type of value
 * 
 * @author CrowsOfWar
 */
public class ConfigurableProperty<T> {
	
	private final String key;
	private T value;
	
	public ConfigurableProperty(String key) {
		this.key = key;
		AvatarConfig.allProperties.put(key, (ConfigurableProperty<Object>) this);
	}
	
	public String getKey() {
		return key;
	}
	
	public T currentValue() {
		return value;
	}
	
	void setValue(T value) {
		this.value = value;
	}
	
}