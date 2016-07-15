package com.crowsofwar.avatar.common.entityproperty;

/**
 * The basic interface for an entity property. A property
 * is of a certain type, and can be retrieved and set.
 * Different implementations would have specific details
 * about how the property should be get and set.
 * 
 * @param <T> The type of object that the property stores.
 */
public interface IEntityProperty<T> {
	
	/**
	 * Get the current value from the entity property.
	 * @return
	 */
	T getValue();
	
	/**
	 * Set the value of the property.
	 * @param value
	 */
	void setValue(T value);
	
}
