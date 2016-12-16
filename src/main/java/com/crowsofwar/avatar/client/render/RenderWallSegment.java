package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
	public void doRender(EntityWallSegment entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		for (int i = 0; i < EntityWallSegment.SEGMENT_HEIGHT; i++) {
			IBlockState block = entity.getBlock(i);
			if (block != null) renderBlock(block, entity, x, y + i, z);
		}
		
	}
	
	private void renderBlock(IBlockState iblockstate, EntityWallSegment entity, double x, double y,
			double z) {
		Tessellator tessellator = Tessellator.getInstance();
		
		if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
			
			if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				VertexBuffer vertexbuffer = tessellator.getBuffer();
				
				if (this.renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.enableOutlineMode(this.getTeamColor(entity));
				}
				
				vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
				BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY,
						entity.posZ);
				GlStateManager.translate(x - blockpos.getX() - 0.5, y - blockpos.getY(),
						z - blockpos.getZ() - 0.5);
				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
						.getBlockRendererDispatcher();
				blockrendererdispatcher.getBlockModelRenderer().renderModel(entity.worldObj,
						blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos,
						vertexbuffer, false, 0);
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
	
}
