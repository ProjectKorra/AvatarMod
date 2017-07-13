/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import static java.lang.Math.*;

/**
 * A mutable 3-dimensional vector using doubles.
 * 
 * @author CrowsOfWar
 */
public class Vector {
	
	/**
	 * The zero vector.
	 */
	public static final Vector ZERO = new Vector();
	
	public static final Vector UP = new Vector(0, 1, 0);
	public static final Vector DOWN = new Vector(0, -1, 0);
	public static final Vector EAST = new Vector(1, 0, 0);
	public static final Vector WEST = new Vector(-1, 0, 0);
	public static final Vector NORTH = new Vector(0, 0, -1);
	public static final Vector SOUTH = new Vector(0, 0, 1);
	
	public static final Vector[] DIRECTION_VECTORS = { UP, DOWN, EAST, WEST, NORTH, SOUTH };
	
	private double cachedMagnitude;
	private double x, y, z;
	
	/**
	 * Creates a new vector at the origin point.
	 */
	public Vector() {
		
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
	public Vector(double x, double y, double z) {
		set(x, y, z);
	}
	
	/**
	 * Creates a copy of the given vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public Vector(Vector vec) {
		set(vec);
		this.cachedMagnitude = vec.cachedMagnitude;
	}
	
	/**
	 * Creates a copy of the given Minecraft vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public Vector(Vec3d vec) {
		this(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Creates a vector from the feet position of the given entity.
	 * 
	 * @param entity
	 *            The entity to use
	 */
	public Vector(Entity entity) {
		this(entity.posX, entity.posY, entity.posZ);
		// if (entity instanceof EntityPlayer && entity.world.isRemote)
		// setY(y - 1.62);
	}
	
	/**
	 * Creates a vector from the coordinates defined by blockPos.
	 * 
	 * @param blockPos
	 *            The vanilla blockPos
	 */
	public Vector(BlockPos blockPos) {
		this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public Vector(Vec3i vec) {
		this(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public Vector(EnumFacing facing) {
		this(facing.getDirectionVec());
	}
	
	/**
	 * Get the x-coordinate of this vector.
	 */
	public double x() {
		return x;
	}
	
	/**
	 * Set the x-coordinate of this vector. Returns <code>this</code> for method
	 * chaining.
	 * 
	 * @param x
	 *            X-coordinate
	 */
	public Vector setX(double x) {
		this.x = x;
		recalcMagnitude();
		return this;
	}
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	public double y() {
		return y;
	}
	
	/**
	 * Set the y-coordinate of this vector. Returns <code>this</code> for method
	 * chaining.
	 * 
	 * @param y
	 *            Y-coordinate
	 */
	public Vector setY(double y) {
		this.y = y;
		recalcMagnitude();
		return this;
	}
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	public double z() {
		return z;
	}
	
	/**
	 * Set the z-coordinate of this vector. Returns <code>this</code> for method
	 * chaining.
	 * 
	 * @param z
	 *            Z-coordinate
	 */
	public Vector setZ(double z) {
		this.z = z;
		recalcMagnitude();
		return this;
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
	public Vector set(double x, double y, double z) {
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
	public Vector set(Vector vec) {
		set(vec.x(), vec.y(), vec.z());
		return this;
	}
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	public Vector copy() {
		return new Vector(this);
	}
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	public Vector add(Vector vec) {
		return add(vec.x(), vec.y(), vec.z());
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
	public Vector add(double x, double y, double z) {
		return set(this.x() + x, this.y() + y, this.z() + z);
	}
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	public Vector plus(Vector vec) {
		return plus(vec.x(), vec.y(), vec.z());
	}
	
	/**
	 * Creates a new vector from the sub of this vector and the vector defined
	 * by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	public Vector plus(double x, double y, double z) {
		return new Vector(this).add(x, y, z);
	}
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	public Vector subtract(Vector vec) {
		return subtract(vec.x(), vec.y(), vec.z());
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
	public Vector subtract(double x, double y, double z) {
		return set(this.x() - x, this.y() - y, this.z() - z);
	}
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	public Vector minus(Vector vec) {
		return minus(vec.x(), vec.y(), vec.z());
	}
	
	/**
	 * Creates a new vector from this vector minus the vector defined by
	 * (x,y,z).
	 * 
	 * @param x
	 *            X-coordinate to subtract
	 * @param y
	 *            Y-coordinate to subtract
	 * @param z
	 *            Z-coordinate to subtract
	 */
	public Vector minus(double x, double y, double z) {
		return new Vector(this).subtract(x, y, z);
	}
	
	/**
	 * Multiply this vector by the given scalar, and returns the result.
	 * Modifies the original vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by
	 * @return this
	 */
	public Vector mul(double scalar) {
		return set(x() * scalar, y() * scalar, z() * scalar);
	}
	
	/**
	 * Creates a new vector from this vector times the scalar.
	 * 
	 * @param scalar
	 *            The scalar to multiply the new vector by
	 */
	public Vector times(double scalar) {
		return new Vector(this).mul(scalar);
	}
	
	/**
	 * Divide this vector by the given scalar, and returns the result. Modifies
	 * the original vector.
	 * 
	 * @param scalar
	 *            The scalar to divide this vector by
	 * @return this
	 */
	public Vector divide(double scalar) {
		return set(x() / scalar, y() / scalar, z() / scalar);
	}
	
	/**
	 * Creates a new vector based on this vector divided by the other vector.
	 * 
	 * @param scalar
	 *            The scalar to divide the new vector by
	 */
	public Vector dividedBy(double scalar) {
		return new Vector(this).divide(scalar);
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
		return x() * x() + y() * y() + z() * z();
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
	public Vector normalize() {
		return divide(magnitude());
	}
	
	/**
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double sqrDist(Vector vec) {
		return sqrDist(vec.x(), vec.y(), vec.z());
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
		return (this.x() - x) * (this.x() - x) + (this.y() - y) * (this.y() - y)
				+ (this.z() - z) * (this.z() - z);
	}
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dist(Vector vec) {
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
	public double dot(Vector vec) {
		return dot(vec.x(), vec.y(), vec.z());
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
		return this.x() * x + this.y() * y + this.z() * z;
	}
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	public Vector cross(Vector vec) {
		return cross(vec.x(), vec.y(), vec.z());
	}
	
	/**
	 * Returns the cross product with the vector defined by (x, y, z). This
	 * creates a new vector.
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	public Vector cross(double x, double y, double z) {
		return new Vector(this.y() * z - this.z() * y, this.z() * x - this.x() * z,
				this.x() * y - this.y() * x);
	}
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged
	 * 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	public double angle(Vector vec) {
		double dot = dot(vec);
		return Math.acos(dot / (this.magnitude() * vec.magnitude()));
	}
	
	/**
	 * Returns this vector reflected across the given normal. Does not modify
	 * this vector or the normal.
	 * 
	 * @param normal
	 *            Must be normalized
	 */
	public Vector reflect(Vector normal) {
		if (normal.sqrMagnitude() != 1)
			throw new IllegalArgumentException("Normal vector must be normalized");
		return this.minus(normal.times(2).times(this.dot(normal)));
	}
	
	/**
	 * <strong>Assuming</strong> this vector represents spherical coordinates
	 * (in radians), returns a unit vector in Cartesian space which has the
	 * rotations of this vector.
	 * <p>
	 * Does not modify this vector.
	 * 
	 * @see #toRectangular(Vector)
	 */
	public Vector toRectangular() {
		return Vector.toRectangular(this);
	}
	
	/**
	 * <strong>Assuming</strong> this vector represents rectangular coordinates,
	 * returns the rotations (in radians) for this vector.
	 * <p>
	 * Does not modify this vector.
	 */
	public Vector toSpherical() {
		return Vector.getRotationTo(Vector.ZERO, this);
	}
	
	/**
	 * Returns a minecraft vector with the same coordinates as this vector.
	 */
	public Vec3d toMinecraft() {
		return new Vec3d(x(), y(), z());
	}
	
	/**
	 * Returns an integer vector of this vector by rounding each component.
	 */
	public VectorI round() {
		return new VectorI((int) Math.round(x()), (int) Math.round(y()), (int) Math.round(z()));
	}
	
	/**
	 * Returns an integer vector of this vector by casting each component to an
	 * integer.
	 */
	public VectorI cast() {
		return new VectorI((int) x(), (int) y(), (int) z());
	}
	
	/**
	 * Returns a BlockPos with the same coordinates as this vector.
	 */
	public BlockPos toBlockPos() {
		return cast().toBlockPos();
	}
	
	public Vec3i toMinecraftInteger() {
		return new Vec3i(x(), y(), z());
	}
	
	/**
	 * Writes this vector to the packet byte buffer.
	 * 
	 * @param buf
	 *            Buffer to write to
	 */
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x());
		buf.writeDouble(y());
		buf.writeDouble(z());
	}
	
	/**
	 * Writes this vector directly to the NBT provided.
	 */
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setDouble("x", x());
		nbt.setDouble("y", y());
		nbt.setDouble("z", z());
	}
	
	@Override
	public String toString() {
		return "(" + x() + ", " + y() + ", " + z() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Vector) {
			Vector vec = (Vector) obj;
			return this.x() == vec.x() && this.y() == vec.y() && this.z() == vec.z();
		} else {
			return false;
		}
	}
	
	/**
	 * Reflects the vector across the given normal. Returns a new vector.
	 * 
	 * @see #reflect(Vector)
	 */
	public static Vector reflect(Vector vec, Vector normal) {
		return vec.reflect(normal);
	}
	
	/**
	 * Returns the euler angles from position 1 to position 2.
	 * <p>
	 * The returned vector has Y for yaw, and X for pitch. Measurements are in
	 * radians.
	 * 
	 * @param pos1
	 *            Where we are
	 * @param pos2
	 *            Where to look at
	 */
	public static Vector getRotationTo(Vector pos1, Vector pos2) {
		Vector diff = pos2.minus(pos1);
		diff.normalize();
		double x = diff.x();
		double y = diff.y();
		double z = diff.z();
		double d0 = x;
		double d1 = y;
		double d2 = z;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		double rotY = Math.atan2(d2, d0) - Math.PI / 2;
		double rotX = -Math.atan2(d1, d3);
		double rotZ = 0;
		return new Vector(rotX, rotY, rotZ);
	}
	
	/**
	 * Gets the position of the entity
	 */
	public static Vector getEntityPos(Entity entity) {
		return new Vector(entity);
	}
	
	/**
	 * Gets the position of the entity, but adjusted so ypos is the eyepos
	 */
	public static Vector getEyePos(Entity entity) {
		Vector pos = getEntityPos(entity);
		pos.setY(pos.y() + entity.getEyeHeight());
		return pos;
	}
	
	/**
	 * Get velocity of the entity in m/s.
	 * 
	 * @see #getVelocityTpS(Entity)
	 */
	public static Vector getVelocityMpS(Entity entity) {
		return new Vector(entity.motionX / 20, entity.motionY / 20, entity.motionZ / 20);
	}
	
	/**
	 * Get velocity of the entity in ticks/s.
	 * 
	 * @see #getVelocityMpS(Entity)
	 */
	public static Vector getVelocityTpS(Entity entity) {
		return new Vector(entity.motionX, entity.motionY, entity.motionZ);
	}
	
	/**
	 * Get the pitch to lob a projectile in radians. Example: pitch to target
	 * can be used in {@link #toRectangular(double, double)}
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
	 * Create a rectangular vector from the entity's rotations. This can be used
	 * to determine the coordinates the entity is looking at (without raytrace).
	 * 
	 * @param entity
	 *            The entity to use
	 */
	public static Vector getLookRectangular(Entity entity) {
		return toRectangular(toRadians(entity.rotationYaw), toRadians(entity.rotationPitch));
	}
	
	/**
	 * Create a rotation vector from the entity's rotations. This is a euler and
	 * is in radians.
	 * 
	 * @see #getEuler(double, double)
	 */
	public static Vector getLookRotations(Entity entity) {
		return getEuler(toRadians(entity.rotationYaw), toRadians(entity.rotationPitch));
	}
	
	/**
	 * Gets a vector representing rotations for the given yaw/pitch. Parameters
	 * should be in radians.
	 */
	public static Vector getEuler(double yaw, double pitch) {
		return new Vector(pitch, yaw, 0);
	}
	
	/**
	 * Converts a rotation vector into a rectangular (Cartesian) vector. Euler
	 * must be in radians.
	 * 
	 * @see #toRectangular(double, double)
	 * @see #getEuler(double, double)
	 */
	public static Vector toRectangular(Vector euler) {
		return new Vector(-sin(euler.y()) * cos(euler.x()), -sin(euler.x()), cos(euler.y()) * cos(euler.x()));
	}
	
	/**
	 * Converts the given rotations into a rectangular (Cartesian) vector.
	 * Parameters must be in radians.
	 * 
	 * @see #toRectangular(Vector)
	 */
	public static Vector toRectangular(double yaw, double pitch) {
		return new Vector(-sin(yaw) * cos(pitch), -sin(pitch), cos(yaw) * cos(pitch));
	}
	
	/**
	 * Creates a new vector from the packet information in the byte buffer.
	 * Vectors should be encoded using the non-static {@link #toBytes(ByteBuf)
	 * toBytes}.
	 * 
	 * @param buf
	 *            Buffer to read from
	 * 
	 * @see #toBytes(ByteBuf)
	 */
	public static Vector fromBytes(ByteBuf buf) {
		return new Vector(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	/**
	 * Creates a new vector from the x,y,z information in NBT. Reads directly
	 * off the NBT compound provided.
	 */
	
	public static Vector readFromNbt(NBTTagCompound nbt) {
		return new Vector(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}
	
}
