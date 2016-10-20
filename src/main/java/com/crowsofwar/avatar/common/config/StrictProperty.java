package com.crowsofwar.avatar.common.config;

/**
 * A property where the value <em>must</em> be of a certain type.
 * 
 * @author CrowsOfWar
 */
public class StrictProperty<T> implements ConfigurableProperty<T> {
	
	private final String key;
	private T value;
	
	public StrictProperty(String key) {
		this.key = key;
		AvatarConfig2.allProperties.put(key, (ConfigurableProperty<Object>) this);
	}
	
	public String getKey() {
		return key;
	}
	
	public T currentValue() {
		return value;
	}
	
	@Override
	public void setValue(Object value) {
		this.value = (T) value;
	}
	
}
