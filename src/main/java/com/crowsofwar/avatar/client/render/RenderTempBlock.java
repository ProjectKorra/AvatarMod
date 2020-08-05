package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.blocks.tiles.TileBlockTemp;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;

/**
 * @author Aang23
 */
public class RenderTempBlock extends TileEntitySpecialRenderer<TileBlockTemp> {
    @Override
    public void render(TileBlockTemp te, double x, double y, double z, float partialTicks, int destroyStage,
            float alpha) {
        IBlockState blockState = te.getRenderBlock();

        Tessellator tessellator = Tessellator.getInstance();

        if (blockState == null)
            return;

        if (blockState.getRenderType() == EnumBlockRenderType.MODEL) {
            if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                BufferBuilder vb = tessellator.getBuffer();

                vb.begin(7, DefaultVertexFormats.BLOCK);
                GlStateManager.translate(x - te.getPos().getX(), y - te.getPos().getY(), z - te.getPos().getZ());
                BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();

                GlStateManager.translate(0, -1, 0);
                brd.getBlockModelRenderer().renderModel(te.getWorld(), brd.getModelForState(blockState), blockState,
                        te.getPos().up(), vb, false, 0);
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }
    }
}