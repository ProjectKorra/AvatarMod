package com.crowsofwar.avatar.common.util;

import net.minecraft.util.math.BlockPos;

/**
 * 
 * 
 * @author CrowsOfWar
 */
// TODO actually make this class GOOD!
public class VectorI {
	
	public int x, y, z;
	
	public VectorI() {
		this(0, 0, 0);
	}
	
	public VectorI(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public VectorI(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}
	
}
