package com.crowsofwar.gorecore.util;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface Vector<UNIT> {
	
	/**
	 * Get the x-coordinate of this vector.
	 */
	UNIT x();
	
	void setX(UNIT x);
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	UNIT y();
	
	void setY(UNIT y);
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	UNIT z();
	
	void setZ(UNIT z);
	
	/**
	 * Set this vector to the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate to set to
	 * @param y
	 *            Y-coordinate to set to
	 * @param z
	 *            Z-coordinate to set to
	 * @return this
	 */
	Vector<UNIT> set(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Set this vector to the given vector.
	 * 
	 * @param vec
	 *            Vector to set to
	 * @return this
	 */
	Vector<UNIT> set(Vector<UNIT> vec);
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	Vector<UNIT> createCopy();
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	Vector<UNIT> add(Vector<UNIT> vec);
	
	/**
	 * Add the given vector defined by (x, y, z) to this vector.
	 * 
	 * @param x
	 *            X-coordinate to add
	 * @param y
	 *            Y-coordinate to add
	 * @param z
	 *            Z-coordinate to add
	 * @return this
	 */
	Vector<UNIT> add(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	Vector<UNIT> plus(Vector<UNIT> vec);
	
	/**
	 * Creates a new vector from the sub of this vector and the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	Vector<UNIT> plus(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	Vector<UNIT> subtract(Vector<UNIT> vec);
	
	/**
	 * Subtract the given vector defined by (x, y, z) from this vector.
	 * 
	 * @param x
	 *            X-coordinate to subtract
	 * @param y
	 *            Y-coordinate to subtract
	 * @param z
	 *            Z-coordinate to subtract
	 * @return this
	 */
	Vector<UNIT> subtract(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	Vector<UNIT> minus(Vector<UNIT> vec);
	
	/**
	 * Creates a new vector from this vector minus the vector defined by (x,y,z).
	 * 
	 * @param x
	 *            X-coordinate to subtract
	 * @param y
	 *            Y-coordinate to subtract
	 * @param z
	 *            Z-coordinate to subtract
	 */
	Vector<UNIT> minus(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Multiply this vector by the given scalar, and returns the result. Modifies the original
	 * vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by
	 * @returns this
	 */
	Vector<UNIT> mul(UNIT scalar);
	
	/**
	 * Creates a new vector from this vector times the scalar.
	 * 
	 * @param scalar
	 *            The scalar to multiply the new vector by
	 */
	Vector<UNIT> times(UNIT scalar);
	
	/**
	 * Divide this vector by the given scalar, and returns the result. Modifies the original vector.
	 * 
	 * @param scalar
	 *            The scalar to divide this vector by
	 * @return this
	 */
	Vector<UNIT> divide(UNIT scalar);
	
	/**
	 * Creates a new vector based on this vector divided by the other vector.
	 * 
	 * @param scalar
	 *            The scalar to divide the new vector by
	 */
	Vector<UNIT> dividedBy(UNIT scalar);
	
	/**
	 * Get the length of this vector.
	 * <p>
	 * The result is cached since square-root is a performance-heavy operation.
	 */
	UNIT magnitude();
	
	/**
	 * Get the square magnitude of this vector.
	 */
	UNIT sqrMagnitude();
	
	/**
	 * Normalizes this vector so that it has a length of 1.
	 * 
	 * @return this
	 */
	Vector<UNIT> normalize();
	
	/**
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	UNIT sqrDist(Vector<UNIT> vec);
	
	/**
	 * Get the square distance from the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            The x-position of the other vector
	 * @param y
	 *            The y-position of the other vector
	 * @param z
	 *            The z-position of the other vector
	 */
	UNIT sqrDist(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	UNIT dist(Vector<UNIT> vec);
	
	/**
	 * Get the distance from the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            The x-position of the other vector
	 * @param y
	 *            The y-position of the other vector
	 * @param z
	 *            The z-position of the other vector
	 */
	UNIT dist(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Get the dot product with the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	UNIT dot(Vector<UNIT> vec);
	
	/**
	 * Get the dot product with the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate of the other vector
	 * @param y
	 *            Y-coordinate of the other vector
	 * @param z
	 *            Z-coordinate of the other vector
	 */
	UNIT dot(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	Vector<UNIT> cross(Vector<UNIT> vec);
	
	/**
	 * Returns the cross product with the vector defined by (x, y, z). This creates a new vector.
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	Vector<UNIT> cross(UNIT x, UNIT y, UNIT z);
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	UNIT angle(Vector<UNIT> vec);
	
}