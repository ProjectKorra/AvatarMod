package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CloudBlock extends AvatarBlock {
	public CloudBlock() {
		super(Material.GLASS, "cloud_block");
		this.setCreativeTab(AvatarItems.tabItems);
		this.setHardness(1f);
		this.setLightLevel(0.1F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }
}