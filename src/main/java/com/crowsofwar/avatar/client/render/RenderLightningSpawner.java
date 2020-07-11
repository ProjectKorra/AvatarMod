package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityLightningSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class RenderLightningSpawner extends Render<EntityLightningSpawner> {
private final Random random;

/**
 * @param renderManager
 */
public RenderLightningSpawner(RenderManager renderManager) {
		super(renderManager);
		this.random = new Random();
		}

@Override
public void doRender(EntityLightningSpawner entity, double x, double y, double z, float entityYaw,
		float partialTicks) {
		World world = entity.getEntityWorld();
		IBlockState blockState = world.getBlockState(entity.getPosition().offset(EnumFacing.DOWN));
		Block block = blockState.getBlock();
		world.spawnParticle(EnumParticleTypes.SPELL_MOB, entity.posX, entity.posY + 0.3, entity.posZ,
		random.nextGaussian() - 0.5, random.nextGaussian() * 0.4, random.nextGaussian() - 0.5,
		Block.getStateId(blockState));
		}

@Override
protected ResourceLocation getEntityTexture(EntityLightningSpawner entity) {
		return null;
		}

		}


