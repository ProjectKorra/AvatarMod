package com.maxandnoah.avatar.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFloatingBlock extends Entity {
	
	public static final Block DEFAULT_BLOCK = Blocks.stone;
	public static final int DATAWATCHER_BLOCKID = 2;
	
	public EntityFloatingBlock(World world) {
		super(world);
		setSize(1, 1);
	}
	
	public EntityFloatingBlock(World world, Block block) {
		this(world);
		setBlock(block);
	}
	
	// Called from constructor of Entity class
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_BLOCKID, getNameForBlock(DEFAULT_BLOCK));
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setBlock(Block.getBlockFromName(nbt.getString("Block")));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("Block", getNameForBlock(getBlock()));
	}

	public Block getBlock() {
		Block block = (Block) Block.blockRegistry.getObject(dataWatcher.getWatchableObjectString(DATAWATCHER_BLOCKID));
		if (block == null) block = DEFAULT_BLOCK;
		return block;
	}

	public void setBlock(Block block) {
		if (block == null) block = DEFAULT_BLOCK;
		dataWatcher.updateObject(DATAWATCHER_BLOCKID, getNameForBlock(block));
	}
	
	private String getNameForBlock(Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
	public int getMetadata() {
		return 0; // TODO Implement metadata tracking into DataWatcher
	}
	
}
