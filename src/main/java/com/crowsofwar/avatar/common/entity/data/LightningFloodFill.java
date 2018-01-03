package com.crowsofwar.avatar.common.entity.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

/**
 * Manages lightning hurting nearby entities when in water, by using a flood-fill algorithm,
 * where some computation is done each tick.
 * <p>
 * This should be used on the server side
 *
 * @author CrowsOfWar
 */
public class LightningFloodFill {

	private final World world;
	private final Consumer<EntityLivingBase> entityCallback;
	private final BlockPos originalPos;
	private final int expansionSq;

	/**
	 * Queue of water blocks to process
	 */
	private final Queue<BlockPos> waterBlocksQueue;
	/**
	 * Any BlockPos's which shouldn't be processed - have either already been processed or queued
	 * to be processed (in {@link #waterBlocksQueue})
	 */
	private final Set<BlockPos> processedBlocks;

	public LightningFloodFill(World world, BlockPos initialPos, int expansion,
							  Consumer<EntityLivingBase> entityCallback) {
		this.world = world;
		this.waterBlocksQueue = new PriorityQueue<>(expansion * expansion * expansion);
		this.processedBlocks = new TreeSet<>();
		this.entityCallback = entityCallback;
		this.originalPos = initialPos;
		this.expansionSq = expansion * expansion;

		waterBlocksQueue.add(initialPos);
		processedBlocks.add(initialPos);
	}

	/**
	 * Ticks the flood fill algorithm. Returns true if the flood fill algorithm has finished.
	 */
	public boolean tick() {

		for (int i = 0; i < 20 && !waterBlocksQueue.isEmpty(); i++) {
			processBlock();
		}

		return waterBlocksQueue.isEmpty();

	}

	private void processBlock() {

		BlockPos pos = waterBlocksQueue.poll();

		// Detect entities at this BlockPos

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new
				AxisAlignedBB(pos));
		for (EntityLivingBase entity : entities) {
			entityCallback.accept(entity);
		}

		// Add more BlockPos to check
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos searchPos = pos.offset(facing);

			if (searchPos.distanceSq(originalPos) <= expansionSq) {
				boolean waterBlock = world.getBlockState(searchPos).getBlock() == Blocks.WATER;
				boolean processedHere = processedBlocks.contains(searchPos);
				if (waterBlock && !processedHere) {
					waterBlocksQueue.add(searchPos);
					processedBlocks.add(searchPos);
				}
			}

		}

	}

}
