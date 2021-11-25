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

import com.crowsofwar.avatar.entity.EntityWaterBubble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * @author CrowsOfWar
 */
public class RenderWaterBubble extends Render<EntityWaterBubble> {

    private static final ResourceLocation water = new ResourceLocation("minecraft",
            "textures/blocks/water_still.png");
    private static final float EXPANSION_TIME = 3;

    public RenderWaterBubble(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityWaterBubble bubble, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        super.doRender(bubble, x, y, z, entityYaw, partialTicks);
        //   Minecraft.getMinecraft().getTextureManager().bindTexture(water);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
       // GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      //  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y + bubble.height / 1.5, z);

        float latStep = (float) Math.PI / 30;
        float longStep = (float) Math.PI / 30;

        float pulse = MathHelper.sin((bubble.ticksExisted + partialTicks) / 10f);

        float r = 0.015F + 0.015F * pulse, g = 0.05F + 0.05f * pulse, b = 0.5F;

        float radius = bubble.width * 0.70F;
        float a = 2F;

//        if (bubble.ticksExisted > bubble.getLifeTime() - EXPANSION_TIME) {
//            radius *= 1 + 0.2f * (bubble.ticksExisted + partialTicks - (bubble.getLifeTime() - EXPANSION_TIME)) / EXPANSION_TIME;
//            a *= Math.max(0, 1 - (bubble.ticksExisted + partialTicks - (bubble.getLifeTime() - EXPANSION_TIME)) / EXPANSION_TIME);
//        } else if (bubble.ticksExisted < EXPANSION_TIME) {
//            radius *= 1 - (EXPANSION_TIME - bubble.ticksExisted - partialTicks) / EXPANSION_TIME;
//            a *= 1 - (EXPANSION_TIME - bubble.ticksExisted - partialTicks) / EXPANSION_TIME;
//        }

        // Draw the inside first
        RenderUtils.drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, true, r, g, b, a);
   //     RenderUtils.drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, false, 1, 1, 1, a * 0.7F);
        RenderUtils.drawSphere(radius, latStep, longStep, false, r, g, b, a);


        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWaterBubble entity) {
        return null;
    }

}
