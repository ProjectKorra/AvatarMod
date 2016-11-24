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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * A mutable 3-dimensional integer vector.
 * <p>
 * For precise measurements, please use {@link Vector}.
 * 
 * @author CrowsOfWar
 */
public class VectorI {
	
	private double cachedMagnitude;
	private int x, y, z;
	
	/**
	 * Creates a new vector at the origin point.
	 */
	public VectorI() {
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
	public VectorI(int x, int y, int z) {
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
	public VectorI(VectorI vec) {
		this(vec.x, vec.y, vec.z);
		this.cachedMagnitude = vec.cachedMagnitude;
	}
	
	/**
	 * Creates a copy of the given Minecraft BlockPos.
	 * 
	 * @param pos
	 *            BlockPos to copy
	 */
	public VectorI(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Get the x-coordinate of this vector.
	 */
	public int x() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
		recalcMagnitude();
	}
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	public int y() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
		recalcMagnitude();
	}
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	public int z() {
		return z;
	}
	
	public void setZ(int z) {
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
	public VectorI set(int x, int y, int z) {
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
	public VectorI set(VectorI vec) {
		set(vec.x, vec.y, vec.z);
		return this;
	}
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	public VectorI createCopy() {
		return new VectorI(this);
	}
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	public VectorI add(VectorI vec) {
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
	public VectorI add(int x, int y, int z) {
		return set(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	public VectorI plus(VectorI vec) {
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
	public VectorI plus(int x, int y, int z) {
		return new VectorI(this).add(x, y, z);
	}
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	public VectorI subtract(VectorI vec) {
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
	public VectorI subtract(int x, int y, int z) {
		return set(this.x - x, this.y - y, this.z - z);
	}
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	public VectorI minus(VectorI vec) {
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
	public VectorI minus(int x, int y, int z) {
		return new VectorI(this).subtract(x, y, z);
	}
	
	/**
	 * Move this Vector in the specified direction by 1 meter.
	 * 
	 * @param direction
	 *            The direction to offset this vector
	 */
	public void offset(EnumFacing direction) {
		offset(direction, 1);
	}
	
	/**
	 * Move this Vector in the specified direction by the specified amount of meters.
	 * 
	 * @param direction
	 *            The direction to offset this vector
	 * @param distance
	 *            How far to offset by
	 */
	public void offset(EnumFacing direction, int distance) {
		switch (direction.getAxis()) {
			case X:
				x += direction.getAxisDirection().getOffset() * distance;
				break;
			case Y:
				y += direction.getAxisDirection().getOffset() * distance;
				break;
			case Z:
				z += direction.getAxisDirection().getOffset() * distance;
				break;
		}
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
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double sqrDist(VectorI vec) {
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
	public double sqrDist(int x, int y, int z) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
	}
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dist(VectorI vec) {
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
	public double dist(int x, int y, int z) {
		return Math.sqrt(sqrDist(x, y, z));
	}
	
	/**
	 * Get the dot product with the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dot(VectorI vec) {
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
	public double dot(int x, int y, int z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	public VectorI cross(VectorI vec) {
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
	public VectorI cross(int x, int y, int z) {
		return new VectorI(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	public double angle(VectorI vec) {
		double dot = dot(vec);
		return Math.acos(dot / (this.magnitude() * vec.magnitude()));
	}
	
	/**
	 * Converts this vector into a minecraft BlockPos.
	 */
	public BlockPos toBlockPos() {
		return new BlockPos(x, y, z);
	}
	
	/**
	 * Creates and returns a new double vector for greater precision.
	 */
	public Vector precision() {
		return new Vector(x, y, z);
	}
	
	/**
	 * Writes this vector to the packet byte buffer.
	 * 
	 * @param buf
	 *            Buffer to write to
	 */
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	/**
	 * Creates a new vector from the packet information in the byte buffer.
	 * 
	 * @param buf
	 *            Buffer to read from
	 */
	public static VectorI fromBytes(ByteBuf buf) {
		return new VectorI(buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
}
