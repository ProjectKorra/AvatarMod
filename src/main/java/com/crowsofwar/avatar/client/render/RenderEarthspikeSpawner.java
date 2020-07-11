package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class RenderEarthspikeSpawner extends Render<EntityEarthspikeSpawner> {
	private final Random random;

	/**
	 * @param renderManager
	 */
	public RenderEarthspikeSpawner(RenderManager renderManager) {
		super(renderManager);
		this.random = new Random();
	}

	@Override
	public void doRender(EntityEarthspikeSpawner entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {
		World world = entity.getEntityWorld();
		IBlockState blockState = world.getBlockState(entity.getPosition().offset(EnumFacing.DOWN));
		Block block = blockState.getBlock();
		world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX, entity.posY + 0.3, entity.posZ,
				random.nextGaussian() - 0.5, random.nextGaussian() * 0.4, random.nextGaussian() - 0.5,
				Block.getStateId(blockState));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEarthspikeSpawner entity) {
		return null;
	}

}


