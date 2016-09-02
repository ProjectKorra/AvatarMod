package com.crowsofwar.avatar.common.bending;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public enum BendingType {
	
	EARTHBENDING,
	FIREBENDING,
	WATERBENDING,
	AIRBENDING;
	
	/**
	 * Get the Id of this BendingType.
	 */
	public int id() {
		return ordinal() + 1;
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
