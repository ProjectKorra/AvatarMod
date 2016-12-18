package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderWallSegment extends Render<EntityWallSegment> {
	
	public RenderWallSegment(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public boolean shouldRender(EntityWallSegment livingEntity, ICamera camera, double camX, double camY,
			double camZ) {
		return true;
	}
	
	@Override
	public void doRender(EntityWallSegment entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		for (int i = 0; i < EntityWallSegment.SEGMENT_HEIGHT; i++) {
			IBlockState block = entity.getBlock(i);
			GlStateManager.translate(0, entity.getBlocksOffset(), 0);
			if (block != null) renderBlock(block, entity, x, y + i, z, new BlockPos(entity).up(i));
			GlStateManager.translate(0, -entity.getBlocksOffset(), 0);
		}
		
	}
	
	private void renderBlock(IBlockState blockState, EntityWallSegment entity, double x, double y, double z,
			BlockPos pos) {
		Tessellator tessellator = Tessellator.getInstance();
		
		if (blockState.getRenderType() == EnumBlockRenderType.MODEL) {
			
			if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				VertexBuffer vb = tessellator.getBuffer();
				
				if (this.renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.enableOutlineMode(this.getTeamColor(entity));
				}
				
				vb.begin(7, DefaultVertexFormats.BLOCK);
				GlStateManager.translate(x - pos.getX() - 0.5, y - pos.getY(), z - pos.getZ() - 0.5);
				BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
				
				GlStateManager.translate(0, -1, 0);
				brd.getBlockModelRenderer().renderModel(entity.worldObj, brd.getModelForState(blockState),
						blockState, pos.up(), vb, false, 0);
				tessellator.draw();
				
				if (this.renderOutlines) {
					GlStateManager.disableOutlineMode();
					GlStateManager.disableColorMaterial();
				}
				
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityWallSegment entity) {
		return null;
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw,
			float partialTicks) {}
	
}
