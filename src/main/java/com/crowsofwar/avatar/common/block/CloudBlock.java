package com.crowsofwar.avatar.common.block;

import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class CloudBlock extends AvatarBlock {
	public CloudBlock() {
		super(Material.CLOTH, "cloud_block");
		this.setCreativeTab(AvatarItems.tabItems);
		this.setHardness(1f);
		this.setLightLevel(0.1F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isCollidable() {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	public Block setBlockUnbreakable() {
		return super.setBlockUnbreakable();
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		double spawnX = entityIn.posX;
		double spawnY = entityIn.posY;
		double spawnZ = entityIn.posZ;
		worldIn.spawnParticle(EnumParticleTypes.CLOUD, spawnX, spawnY, spawnZ, 0, 0, 0);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}
}
