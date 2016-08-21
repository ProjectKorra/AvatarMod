package com.crowsofwar.gorecore.util;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * A mutable 3-dimensional vector using doubles.
 * 
 * @author CrowsOfWar
 */
public class VectorD {
	
	private double cachedMagnitude;
	private double x, y, z;
	
	/**
	 * Creates a new vector at the origin point.
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
	public VectorD(double x, double y, double z) {
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
	public VectorD(VectorD vec) {
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
	public double x() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
		recalcMagnitude();
	}
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	public double y() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
		recalcMagnitude();
	}
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	public double z() {
		return z;
	}
	
	public void setZ(double z) {
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
	public VectorD set(double x, double y, double z) {
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
	public VectorD set(VectorD vec) {
		set(vec.x, vec.y, vec.z);
		return this;
	}
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	public VectorD createCopy() {
		return new VectorD(this);
	}
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	public VectorD add(VectorD vec) {
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
	public VectorD add(double x, double y, double z) {
		return set(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	public VectorD plus(VectorD vec) {
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
	public VectorD plus(double x, double y, double z) {
		return new VectorD(this).add(x, y, z);
	}
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	public VectorD subtract(VectorD vec) {
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
	public VectorD subtract(double x, double y, double z) {
		return set(this.x - x, this.y - y, this.z - z);
	}
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	public VectorD minus(VectorD vec) {
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
	public VectorD minus(double x, double y, double z) {
		return new VectorD(this).subtract(x, y, z);
	}
	
	/**
	 * Multiply this vector by the given scalar, and returns the result. Modifies the original
	 * vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by
	 * @returns this
	 */
	public VectorD mul(double scalar) {
		return set(x * scalar, y * scalar, z * scalar);
	}
	
	/**
	 * Creates a new vector from this vector times the scalar.
	 * 
	 * @param scalar
	 *            The scalar to multiply the new vector by
	 */
	public VectorD times(double scalar) {
		return new VectorD(this).mul(scalar);
	}
	
	/**
	 * Divide this vector by the given scalar, and returns the result. Modifies the original vector.
	 * 
	 * @param scalar
	 *            The scalar to divide this vector by
	 * @return this
	 */
	public VectorD divide(double scalar) {
		return set(x / scalar, y / scalar, z / scalar);
	}
	
	/**
	 * Creates a new vector based on this vector divided by the other vector.
	 * 
	 * @param scalar
	 *            The scalar to divide the new vector by
	 */
	public VectorD dividedBy(double scalar) {
		return new VectorD(this).divide(scalar);
	}
	
	/**
	 * Get the length of this vector.
	 * <p>
	 * The result is cached since square-root is a performance-heavy operation.
	 */
	public double magnitude() {
		if (cachedMagnitude == -1) {
			cachedMagnitude = Math.sqrt(sqrMagnitude());
		}
		return cachedMagnitude;
	}
	
	/**
	 * Get the square magnitude of this vector.
	 */
	public double sqrMagnitude() {
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
	public VectorD normalize() {
		return divide(magnitude());
	}
	
	/**
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double sqrDist(VectorD vec) {
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
	public double sqrDist(double x, double y, double z) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
	}
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dist(VectorD vec) {
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
	public double dist(double x, double y, double z) {
		return Math.sqrt(sqrDist(x, y, z));
	}
	
	/**
	 * Get the dot product with the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dot(VectorD vec) {
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
	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	public VectorD cross(VectorD vec) {
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
	public VectorD cross(double x, double y, double z) {
		return new VectorD(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	public double angle(VectorD vec) {
		double dot = dot(vec);
		return Math.acos(dot / (this.magnitude() * vec.magnitude()));
	}
	
	/**
	 * Converts this vector into a minecraft vector.
	 */
	public VectorD toMinecraft() {
		return new VectorD(x, y, z);
	}
	
	/**
	 * Writes this vector to the packet byte buffer.
	 * 
	 * @param buf
	 *            Buffer to write to
	 */
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
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
	public static VectorD getRotations(VectorD pos1, VectorD pos2) {
		VectorD diff = pos2.minus(pos1);
		diff.normalize();
		double x = diff.x;
		double y = diff.y;
		double z = diff.z;
		double d0 = x;
		double d1 = y;
		double d2 = z;
		double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		double rotY = Math.atan2(d2, d0) - Math.PI / 2;
		double rotX = -Math.atan2(d1, d3);
		double rotZ = 0;
		return new VectorD(rotX, rotY, rotZ);
	}
	
	public static VectorD getEntityPos(Entity entity) {
		VectorD pos = new VectorD(entity.posX, entity.posY, entity.posZ);
		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) pos.setY(pos.y - 1.62);
		return pos;
	}
	
	public static VectorD getEyePos(Entity entity) {
		VectorD pos = getEntityPos(entity);
		pos.setY(pos.y + 1.62);
		return pos;
	}
	
	/**
	 * Get the pitch to lob a projectile in radians. Example: pitch to target can be used in
	 * {@link #fromYawPitch(double, double)}
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
	public static double getProjectileAngle(double v, double g, double x, double y) {
		return -Math.atan2((v * v + Math.sqrt(v * v * v * v - g * (g * x * x + 2 * y * v * v))), g * x);
	}
	
	/**
	 * Create a unit vector from yaw and pitch. Parameters should be in radians.
	 */
	public static VectorD fromYawPitch(double yaw, double pitch) {
		return new VectorD(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch));
	}
	
	/**
	 * Create a unit vector from the given euler angles. Measurements should be in radians.
	 */
	public static VectorD fromDirection(VectorD euler) {
		return fromYawPitch(euler.y, euler.x);
	}
	
	/**
	 * Creates a new vector from the packet information in the byte buffer.
	 * 
	 * @param buf
	 *            Buffer to read from
	 */
	public static VectorD fromBytes(ByteBuf buf) {
		return new VectorD(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
}
