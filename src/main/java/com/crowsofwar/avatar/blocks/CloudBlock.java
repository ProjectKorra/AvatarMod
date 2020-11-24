package com.crowsofwar.avatar.blocks;

import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class CloudBlock extends Block {

	public CloudBlock() {
		super(Material.CLOTH);
		this.setCreativeTab(AvatarItems.tabItems);
		this.setTranslationKey("avatarmod:cloudblock");
		this.setHardness(1f);
		this.setLightOpacity(0);
		this.setLightLevel(15F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}


	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityWalk(worldIn, pos, entityIn);
		if (worldIn.isRemote)
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.95F, 0.95F, 0.95F, 0.075F)
				.pos(entityIn.posX, pos.getY() + 1, entityIn.posZ).scale(1.25F).time(25).spin(0.25F, 0.125).spawn(worldIn);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		Vec3d centre = AvatarEntityUtils.getMiddleOfBlock(state, worldIn, pos);
		if (worldIn.isRemote && centre != null)
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.95F, 0.95F, 0.95F, 0.075F)
					.pos(centre).scale(1.5F).time(25).spin(0.25F, 0.125 * worldIn.rand.nextGaussian()).spawn(worldIn);
	}


	@Override
	public int tickRate(World worldIn) {
		return 3;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

}

