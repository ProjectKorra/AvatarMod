package com.crowsofwar.avatar.bending.bending;

import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SourceInfo {

    private IBlockState state;
    private World world;
    private int time;
    private BlockPos pos;

    public SourceInfo() {
        this.state = Blocks.AIR.getDefaultState();
        this.time = -1;
        this.pos = new BlockPos(0, 0, 0);

    }

    public SourceInfo(IBlockState state, World world, BlockPos pos) {
        this.state = state;
        this.world = world;
        this.time = -1;
        this.pos = pos;
    }

    public SourceInfo(IBlockState state, World world, int time, BlockPos pos) {
        this.state = state;
        this.world = world;
        this.time = time;
        this.pos = pos;
    }

    public void setState(IBlockState state) {
        this.state = state;
    }

    public IBlockState getBlockState() {
        return this.state;
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    public void writeToBytes(ByteBuf buf) {
        buf.writeInt(getTime());
        GoreCoreByteBufUtil.writeBlockPos(buf, pos);
        buf.writeInt(Block.getStateId(state));
    }

    public SourceInfo readFromBytes(ByteBuf buf) {
        this.setTime(buf.readInt());
        this.setBlockPos(GoreCoreByteBufUtil.readBlockPos(buf));
        this.setState(Block.getStateById(buf.readInt()));
        return this;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        int[] blockPos = new int[3];
        blockPos[0] = getBlockPos().getX();
        blockPos[1] = getBlockPos().getY();
        blockPos[2] = getBlockPos().getZ();

        nbt.setInteger("Block Time", time);
        nbt.setInteger("Block State", Block.getStateId(state));
        nbt.setIntArray("Block Pos", blockPos);
    }

    public SourceInfo readFromNBT(NBTTagCompound nbt) {

        this.setTime(nbt.getInteger("Block Time"));
        this.setState(Block.getStateById(nbt.getInteger("Block State")));

        int[] blockPos = nbt.getIntArray("Block Pos");
        if (blockPos.length > 2)
            this.setBlockPos(new BlockPos(blockPos[0], blockPos[1], blockPos[2]));
        else this.setBlockPos(new BlockPos(0, 0, 0));

        return this;
    }

}
