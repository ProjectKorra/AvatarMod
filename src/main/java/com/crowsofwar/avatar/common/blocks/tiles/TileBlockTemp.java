package com.crowsofwar.avatar.common.blocks.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * @author Aang23
 */
public class TileBlockTemp extends TileEntity implements ITickable {

    private IBlockState renderBlock = Blocks.AIR.getDefaultState();
    private int lifetime = Integer.MAX_VALUE;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        renderBlock = NBTUtil.readBlockState(compound);
        lifetime = compound.getInteger("lifetime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTUtil.writeBlockState(compound, renderBlock);
        compound.setInteger("lifetime", lifetime);
        return compound;
    }

    @Override
    public void update() {
        lifetime--;
        if (lifetime <= 0) {
            this.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public IBlockState getRenderBlock() {
        return renderBlock;
    }

    public void setRenderBlock(IBlockState renderBlock) {
        this.renderBlock = renderBlock;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

}
