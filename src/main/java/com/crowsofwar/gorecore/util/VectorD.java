package com.crowsofwar.gorecore.util;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * A mutable 3-dimensional vector using Doubles. Also contains convenience methods for converting
 * to/from Minecraft vectors (Vec3d).
 * 
 * @author CrowsOfWar
 */
public class VectorD implements Vector<Double> {
	
	// TODO make the zero vector immutable
	public static final Vector<Double> ZERO = new VectorD();
	
	private Double cachedMagnitude;
	private Double x, y, z;
	
	/**
	 * Creates the zero vector.
	 */
	public VectorD() {
		this(0, 0, 0);
	}
	
	/**
	 * Creates using the coordinates (x, y, z).
	 * 
	 * @param x
	 *            X-position of the new vector
	 * @param y
	 *            Y-position of the new vector
	 * @param z
	 *            Z-position of the new vector
	 */
	public VectorD(Double x, Double y, Double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		recalcMagnitude();
	}
	
	/**
	 * Creates a copy of the given vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public VectorD(Vector<Double> vec) {
		this(vec.x, vec.y, vec.z);
		this.cachedMagnitude = vec.cachedMagnitude;
	}
	
	/**
	 * Creates a copy of the given Minecraft vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public VectorD(Vec3d vec) {
		this(vec.xCoord, vec.yCoord, vec.zCoord);
	}
	
	/**
	 * Get the x-coordinate of this vector.
	 */
	@Override
	public Double x() {
		return x;
	}
	
	@Override
	public void setX(Double x) {
		this.x = x;
		recalcMagnitude();
	}
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	@Override
	public Double y() {
		return y;
	}
	
	@Override
	public void setY(Double y) {
		this.y = y;
		recalcMagnitude();
	}
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	@Override
	public Double z() {
		return z;
	}
	
	@Override
	public void setZ(Double z) {
		this.z = z;
		recalcMagnitude();
	}
	
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
	@Override
	public Vector<Double> set(Double x, Double y, Double z) {
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}
	
	/**
	 * Set this vector to the given vector.
	 * 
	 * @param vec
	 *            Vector to set to
	 * @return this
	 */
	@Override
	public Vector<Double> set(Vector<Double> vec) {
		set(vec.x, vec.y, vec.z);
		return this;
	}
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	@Override
	public Vector<Double> createCopy() {
		return new Vector<Double>(this);
	}
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	@Override
	public Vector<Double> add(Vector<Double> vec) {
		return add(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Vector<Double> add(Double x, Double y, Double z) {
		return set(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	@Override
	public Vector<Double> plus(Vector<Double> vec) {
		return plus(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Vector<Double> plus(Double x, Double y, Double z) {
		return new Vector<Double>(this).add(x, y, z);
	}
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	@Override
	public Vector<Double> subtract(Vector<Double> vec) {
		return subtract(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Vector<Double> subtract(Double x, Double y, Double z) {
		return set(this.x - x, this.y - y, this.z - z);
	}
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	@Override
	public Vector<Double> minus(Vector<Double> vec) {
		return minus(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Vector<Double> minus(Double x, Double y, Double z) {
		return new Vector<Double>(this).subtract(x, y, z);
	}
	
	/**
	 * Multiply this vector by the given scalar, and returns the result. Modifies the original
	 * vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by
	 * @returns this
	 */
	@Override
	public Vector<Double> mul(Double scalar) {
		return set(x * scalar, y * scalar, z * scalar);
	}
	
	/**
	 * Creates a new vector from this vector times the scalar.
	 * 
	 * @param scalar
	 *            The scalar to multiply the new vector by
	 */
	@Override
	public Vector<Double> times(Double scalar) {
		return new Vector<Double>(this).mul(scalar);
	}
	
	/**
	 * Divide this vector by the given scalar, and returns the result. Modifies the original vector.
	 * 
	 * @param scalar
	 *            The scalar to divide this vector by
	 * @return this
	 */
	@Override
	public Vector<Double> divide(Double scalar) {
		return set(x / scalar, y / scalar, z / scalar);
	}
	
	/**
	 * Creates a new vector based on this vector divided by the other vector.
	 * 
	 * @param scalar
	 *            The scalar to divide the new vector by
	 */
	@Override
	public Vector<Double> dividedBy(Double scalar) {
		return new Vector<Double>(this).divide(scalar);
	}
	
	/**
	 * Get the length of this vector.
	 * <p>
	 * The result is cached since square-root is a performance-heavy operation.
	 */
	@Override
	public Double magnitude() {
		if (cachedMagnitude == -1) {
			cachedMagnitude = Math.sqrt(sqrMagnitude());
		}
		return cachedMagnitude;
	}
	
	/**
	 * Get the square magnitude of this vector.
	 */
	@Override
	public Double sqrMagnitude() {
		return x * x + y * y + z * z;
	}
	
	/**
	 * Mark cachedMagnitude so it needs to be recalculated.
	 */
	private void recalcMagnitude() {
		cachedMagnitude = -1;
	}
	
	/**
	 * Normalizes this vector so that it has a length of 1.
	 * 
	 * @return this
	 */
	@Override
	public Vector<Double> normalize() {
		return divide(magnitude());
	}
	
	/**
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	@Override
	public Double sqrDist(Vector<Double> vec) {
		return sqrDist(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Double sqrDist(Double x, Double y, Double z) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
	}
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	@Override
	public Double dist(Vector<Double> vec) {
		return Math.sqrt(sqrDist(vec));
	}
	
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
	@Override
	public Double dist(Double x, Double y, Double z) {
		return Math.sqrt(sqrDist(x, y, z));
	}
	
	/**
	 * Get the dot product with the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	@Override
	public Double dot(Vector<Double> vec) {
		return dot(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Double dot(Double x, Double y, Double z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	@Override
	public Vector<Double> cross(Vector<Double> vec) {
		return cross(vec.x, vec.y, vec.z);
	}
	
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
	@Override
	public Vector<Double> cross(Double x, Double y, Double z) {
		return new Vector<Double>(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	@Override
	public Double angle(Vector<Double> vec) {
		Double dot = dot(vec);
		return Math.acos(dot / (this.magnitude() * vec.magnitude()));
	}
	
	/**
	 * Converts this vector into a minecraft vector.
	 */
	public Vector<Double> toMinecraft() {
		return new Vector<Double>(x, y, z);
	}
	
	/**
	 * Returns the euler angles from position 1 to position 2.
	 * <p>
	 * The returned vector has Y for yaw, and X for pitch. Measurements are in radians.
	 * 
	 * @param pos1
	 *            Where we are
	 * @param pos2
	 *            Where to look at
	 */
	public static Vector<Double> getRotations(Vector<Double> pos1, Vector<Double> pos2) {
		Vector<Double> diff = pos2.minus(pos1);
		diff.normalize();
		Double x = diff.x;
		Double y = diff.y;
		Double z = diff.z;
		Double d0 = x;
		Double d1 = y;
		Double d2 = z;
		Double d3 = (Double) MathHelper.sqrt_Double(d0 * d0 + d2 * d2);
		Double rotY = Math.atan2(d2, d0) - Math.PI / 2;
		Double rotX = -Math.atan2(d1, d3);
		Double rotZ = 0;
		return new Vector<Double>(rotX, rotY, rotZ);
	}
	
	public static Vector<Double> getEntityPos(Entity entity) {
		Vector<Double> pos = new Vector<Double>(entity.posX, entity.posY, entity.posZ);
		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) pos.setY(pos.y - 1.62);
		return pos;
	}
	
	public static Vector<Double> getEyePos(Entity entity) {
		Vector<Double> pos = getEntityPos(entity);
		pos.setY(pos.y + 1.62);
		return pos;
	}
	
	/**
	 * Get the pitch to lob a projectile in radians. Example: pitch to target can be used in
	 * {@link #fromYawPitch(Double, Double)}
	 * 
	 * @param v
	 *            Force of the projectile, going FORWARDS
	 * @param g
	 *            Gravity constant
	 * @param x
	 *            Horizontal distance to target
	 * @param y
	 *            Vertical distance to target
	 */
	public static Double getProjectileAngle(Double v, Double g, Double x, Double y) {
		return -Math.atan2((v * v + Math.sqrt(v * v * v * v - g * (g * x * x + 2 * y * v * v))), g * x);
	}
	
	/**
	 * Create a unit vector from yaw and pitch. Parameters should be in radians.
	 */
	public static Vector<Double> fromYawPitch(Double yaw, Double pitch) {
		return new Vector<Double>(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch));
	}
	
	/**
	 * Create a unit vector from the given euler angles. Measurements should be in radians.
	 */
	public static Vector<Double> fromDirection(Vector<Double> euler) {
		return fromYawPitch(euler.y, euler.x);
	}
	
}
