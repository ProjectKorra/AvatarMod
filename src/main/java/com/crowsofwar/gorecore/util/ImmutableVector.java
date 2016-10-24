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
	
	/**
	 * Create the zero vector.
	 */
	public ImmutableVector() {}
	
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
		super(x, y, z);
	}
	
	/**
	 * Creates an immutable copy of the given vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public ImmutableVector(Vector vec) {
		super(vec);
	}
	
	/**
	 * Creates an immutable copy of the given Minecraft vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public ImmutableVector(Vec3d vec) {
		super(vec.xCoord, vec.yCoord, vec.zCoord);
	}
	
	/**
	 * Creates a vector from the feet position of the given entity.
	 * 
	 * @param entity
	 *            The entity to use
	 */
	public ImmutableVector(Entity entity) {
		super(entity.posX, entity.posY, entity.posZ);
	}
	
	/**
	 * Creates a vector from the coordinates defined by blockPos.
	 * 
	 * @param blockPos
	 *            The vanilla blockPos
	 */
	public ImmutableVector(BlockPos blockPos) {
		super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public ImmutableVector(Vec3i vec) {
		super(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public ImmutableVector(EnumFacing facing) {
		super(facing.getDirectionVec());
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
