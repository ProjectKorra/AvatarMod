package com.crowsofwar.avatar.common.blocks;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.blocks.tiles.TileBlockTemp;
import com.crowsofwar.avatar.common.item.AvatarItems;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Aang23
 */
public class BlockTemp extends Block implements ITileEntityProvider {

	public BlockTemp() {
		super(Material.ROCK);
		setRegistryName("temp_block");
		setCreativeTab(AvatarItems.tabItems);
	}

	/**
	 * Create a temporary block at the specified location.
	 *
	 * @param world
	 * @param pos
	 * @param lifetime Ticks
	 * @param toCopy
	 */
	public static void createTempBlock(World world, BlockPos pos, int lifetime, IBlockState toCopy) {
		IBlockState state = AvatarBlocks.BLOCK_TEMP.getDefaultState();
		world.setBlockState(pos, state);
		TileBlockTemp tile = (TileBlockTemp) world.getTileEntity(pos);
		tile.setLifetime(lifetime);
		tile.setRenderBlock(toCopy);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBlockTemp();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
									EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		return false;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		TileBlockTemp tile = ((TileBlockTemp) worldIn.getTileEntity(pos));
		if (tile != null) {
			return tile.getRenderBlock().getBlock().getCollisionBoundingBox(blockState, worldIn, pos);
		} else {
			return NULL_AABB;
		}
	}


	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		((World) world).setBlockState(pos, Blocks.AIR.getDefaultState());
	}
}