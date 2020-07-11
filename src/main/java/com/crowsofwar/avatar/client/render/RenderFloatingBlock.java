/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFloatingBlock extends Render {
	// [1.10] Find out substitution for Renderblocks- maybe ModelLoader?
	private static final String __OBFID = "CL_00000994";

	public RenderFloatingBlock(RenderManager renderManager) {
		super(renderManager);
		this.shadowSize = 0.5F;
	}

	/**
	 *
	 */
	public void doRender(EntityFloatingBlock entity, double x, double y, double z, float entityYaw,
						 float lerp) {
		World world = entity.world;
		Block block = entity.getBlock();
		int i = MathHelper.floor(entity.posX);
		int j = MathHelper.floor(entity.posY);
		int k = MathHelper.floor(entity.posZ);

		// x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) *
		// ((lerp -
		// entity.lastTickPosX) / (entity.posX - entity.lastTickPosX)) -
		// RenderManager.renderPosX;
		// z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) *
		// ((lerp -
		// entity.lastTickPosZ) / (entity.posZ - entity.lastTickPosZ)) -
		// RenderManager.renderPosZ;

		if (block != null) {
			Tessellator tessellator = Tessellator.getInstance();

			IBlockState iblockstate = entity.getBlockState();

			if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {

				if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
					this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
					BufferBuilder BufferBuilder = tessellator.getBuffer();

					if (this.renderOutlines) {
						GlStateManager.enableColorMaterial();
						GlStateManager.enableOutlineMode(this.getTeamColor(entity));
					}

					BufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
					BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY,
							entity.posZ);
					GlStateManager.translate(x - blockpos.getX() - 0.5, y - blockpos.getY(),
							z - blockpos.getZ() - 0.5);
					BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
							.getBlockRendererDispatcher();
					blockrendererdispatcher.getBlockModelRenderer().renderModel(world,
							blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos,
							BufferBuilder, false, 0);
					tessellator.draw();

					if (this.renderOutlines) {
						GlStateManager.disableOutlineMode();
						GlStateManager.disableColorMaterial();
					}

					GlStateManager.enableLighting();
					GlStateManager.popMatrix();
					super.doRender(entity, x, y, z, entityYaw, lerp);
				}
			}

		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityFloatingBlock p_110775_1_) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return this.getEntityTexture((EntityFloatingBlock) p_110775_1_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void func_76986_a(T entity, double d, double d1, double d2, float f,
	 * float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
						 float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityFloatingBlock) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
				p_76986_9_);
	}
}
