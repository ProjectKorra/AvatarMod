package com.crowsofwar.avatar.common.bending;

/**
 * Defines different types of bending without actually describing their
 * behavior. Useful for things like Id and Id lookup.
 * 
 * @author CrowsOfWar
 */
public enum BendingType {
	
	ERROR,
	EARTHBENDING,
	FIREBENDING,
	WATERBENDING,
	AIRBENDING;
	
	/**
	 * Get the Id of this BendingType.
	 */
	public int id() {
		return ordinal();
	}
	
	/**
	 * Find the BendingType with the given Id.
	 * 
	 * @param id
	 *            Id of bending type
	 * @return BendingType of that Id
	 * @throws IllegalArgumentException
	 *             if the Id is invalid
	 */
	public static BendingType find(int id) {
		if (id < 0 || id >= values().length)
			throw new IllegalArgumentException("Cannot find BendingType with invalid id: " + id);
		return values()[id];
	}
	
}
