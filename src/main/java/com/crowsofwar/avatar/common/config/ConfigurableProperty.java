package com.crowsofwar.avatar.common.config;

/**
 * Represents a Configuration entry. It has a String key and a changeable value.
 * 
 * @param <T>
 *            The type of value
 * 
 * @author CrowsOfWar
 */
public interface ConfigurableProperty<T> {
	
	T currentValue();
	
	/**
	 * Not using generics because of annoying things with converting Integer -> Double.
	 */
	void setValue(Object value);
	
}