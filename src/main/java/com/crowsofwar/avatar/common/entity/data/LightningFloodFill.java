package com.crowsofwar.avatar.common.entity.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manages lightning hurting nearby entities when in water, by using a flood-fill algorithm,
 * where some computation is done each tick.
 *
 * This should be used on the server side
 *
 * @author CrowsOfWar
 */
public class LightningFloodFill {

	private final World world;

	/**
	 * Queue of water blocks to process
	 */
	private final Queue<BlockPos> waterBlocksQueue;
	/**
	 * Any BlockPos's which shouldn't be processed - have either already been processed or queued
	 * to be processed (in {@link #waterBlocksQueue})
	 */
	private final Set<BlockPos> processedBlocks;

	public LightningFloodFill(World world, BlockPos initialPos, int expansion) {
		this.world = world;
		this.waterBlocksQueue = new PriorityQueue<>(expansion * expansion * expansion);
		this.processedBlocks = new TreeSet<>();

		waterBlocksQueue.add(initialPos);
	}


}
