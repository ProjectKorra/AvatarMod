package com.crowsofwar.avatar.common.config;

/**
 * A configurable property of double. Also {@link #setValue(Object) accepts setting as an int, long,
 * float, etc.} - automatically converts it to double.
 * 
 * @author CrowsOfWar
 */
public class DoubleProperty implements ConfigurableProperty<Double> {
	
	private final String key;
	private double value;
	
	/**
	 * @param key
	 */
	public DoubleProperty(String key) {
		this.key = key;
		AvatarConfig.allProperties.put(key, this);
	}
	
	@Override
	public Double currentValue() {
		return value;
	}
	
	/**
	 * Set the value of this property. Automatically converts the value into a double.
	 * 
	 * @throws NumberFormatException
	 *             if the value's toString() doesn't output a value {@link Double#valueOf(String)
	 *             readable as a double}
	 */
	@Override
	public void setValue(Object value) {
		value = Double.valueOf(value.toString());
	}
	
}
