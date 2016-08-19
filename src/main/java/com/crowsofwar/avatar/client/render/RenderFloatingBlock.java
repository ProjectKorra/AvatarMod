package com.crowsofwar.avatar.client.render;

import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFloatingBlock extends Render {
	// [1.10] Find out substitution for Renderblocks- maybe ModelLoader?
	private final RenderBlocks field_147920_a = new RenderBlocks();
	private static final String __OBFID = "CL_00000994";
	
	public RenderFloatingBlock(RenderManager renderManager) {
		super(renderManager);
		this.shadowSize = 0.5F;
	}
	
	/**
	 * 
	 */
	public void doRender(EntityFloatingBlock entity, double x, double y, double z, float interpolatedYaw,
			float lerp) {
		World world = entity.worldObj;
		Block block = entity.getBlock();
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);
		
		// x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ((lerp -
		// entity.lastTickPosX) / (entity.posX - entity.lastTickPosX)) - RenderManager.renderPosX;
		// z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ((lerp -
		// entity.lastTickPosZ) / (entity.posZ - entity.lastTickPosZ)) - RenderManager.renderPosZ;
		
		if (block != null && block != world.getBlockState(new BlockPos(i, j, k)).getBlock()) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y + 0.5f, (float) z);
			this.bindEntityTexture(entity);
			GL11.glDisable(GL11.GL_LIGHTING);
			Tessellator tessellator;
			
			// if (block instanceof BlockAnvil)
			// {
			// this.field_147920_a.blockAccess = world;
			// tessellator = Tessellator.instance;
			// tessellator.startDrawingQuads();
			// tessellator.setTranslation((double)((float)(-i) - 0.5F), (double)((float)(-j) -
			// 0.5F), (double)((float)(-k) - 0.5F));
			// this.field_147920_a.renderBlockAnvilMetadata((BlockAnvil)block, i, j, k,
			// p_76986_1_.field_145814_a);
			// tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			// tessellator.draw();
			// }
			// else if (block instanceof BlockDragonEgg)
			// {
			// this.field_147920_a.blockAccess = world;
			// tessellator = Tessellator.instance;
			// tessellator.startDrawingQuads();
			// tessellator.setTranslation((double)((float)(-i) - 0.5F), (double)((float)(-j) -
			// 0.5F), (double)((float)(-k) - 0.5F));
			// this.field_147920_a.renderBlockDragonEgg((BlockDragonEgg)block, i, j, k);
			// tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			// tessellator.draw();
			// }
			// else
			// {
			this.field_147920_a.setRenderBoundsFromBlock(block);
			this.field_147920_a.renderBlockSandFalling(block, world, i, j, k, entity.getMetadata());
			// }
			
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityFloatingBlock p_110775_1_) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return this.getEntityTexture((EntityFloatingBlock) p_110775_1_);
	}
	
	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down
	 * its argument and then handing it off to a worker function which does the actual work. In all
	 * probabilty, the class Render is generic (Render<T extends Entity) and this method has
	 * signature public void func_76986_a(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
			float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityFloatingBlock) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
				p_76986_9_);
	}
}