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

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ImmutableVector extends Vector {
	
	private final double x, y, z;
	
	/**
	 * Create the zero vector.
	 */
	public ImmutableVector() {
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
	public ImmutableVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates an immutable copy of the given vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public ImmutableVector(Vector vec) {
		this.x = vec.x();
		this.y = vec.y();
		this.z = vec.z();
	}
	
	/**
	 * Creates an immutable copy of the given Minecraft vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public ImmutableVector(Vec3d vec) {
		this.x = vec.xCoord;
		this.y = vec.yCoord;
		this.z = vec.zCoord;
	}
	
	/**
	 * Creates a vector from the feet position of the given entity.
	 * 
	 * @param entity
	 *            The entity to use
	 */
	public ImmutableVector(Entity entity) {
		this.x = entity.posX;
		this.y = entity.posY;
		this.z = entity.posZ;
	}
	
	/**
	 * Creates a vector from the coordinates defined by blockPos.
	 * 
	 * @param blockPos
	 *            The vanilla blockPos
	 */
	public ImmutableVector(BlockPos blockPos) {
		this.x = blockPos.getX();
		this.y = blockPos.getY();
		this.z = blockPos.getZ();
	}
	
	public ImmutableVector(Vec3i vec) {
		this.x = vec.getX();
		this.y = vec.getY();
		this.z = vec.getZ();
	}
	
	public ImmutableVector(EnumFacing facing) {
		this(facing.getDirectionVec());
	}
	
	@Override
	public double x() {
		return x;
	}
	
	@Override
	public double y() {
		return y;
	}
	
	@Override
	public double z() {
		return z;
	}
	
	@Override
	public Vector setX(double x) {
		throw new UnsupportedOperationException("Cannot modify immutable vectors");
	}
	
	@Override
	public Vector setY(double y) {
		throw new UnsupportedOperationException("Cannot modify immutable vectors");
	}
	
	@Override
	public Vector setZ(double z) {
		throw new UnsupportedOperationException("Cannot modify immutable vectors");
	}
	
}
