package com.crowsofwar.avatar.common.blocks;

import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtils {

	public static boolean canPlaceFireAt(World world, BlockPos pos) {
		return Blocks.FIRE.canPlaceBlockAt(world, pos) && !(world.getBlockState(pos).getBlock() instanceof BlockLiquid)
				&& world.getBlockState(pos).getBlock() == Blocks.AIR;
	}
}
