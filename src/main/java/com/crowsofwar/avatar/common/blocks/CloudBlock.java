package com.crowsofwar.avatar.common.blocks;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class CloudBlock extends Block {

	public CloudBlock() {
		super(Material.GLASS);
		this.setCreativeTab(AvatarItems.tabItems);
		this.setTranslationKey("cloudblock");
		this.setHardness(1f);
		this.setLightOpacity(0);
		this.setLightLevel(5F);
		this.setResistance(10F);
		this.setSoundType(SoundType.CLOTH);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "json"));
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityWalk(worldIn, pos, entityIn);
		if (worldIn.isRemote)
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.85F, 0.85F, 0.85F)
				.pos(entityIn.posX, pos.getY() + 1, entityIn.posZ).scale(1.25F).time(25).spin(0.25F, 0.125).spawn(worldIn);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		Vec3d centre = state.getCollisionBoundingBox(worldIn, pos).getCenter().add(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
		if (worldIn.isRemote)
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.85F, 0.85F, 0.85F)
					.pos(centre).scale(1.5F).time(25).spin(0.25F, 0.125 * worldIn.rand.nextGaussian()).spawn(worldIn);
	}


	@Override
	public int tickRate(World worldIn) {
		return 1;
	}
}
