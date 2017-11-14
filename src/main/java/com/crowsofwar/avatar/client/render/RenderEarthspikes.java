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

import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 *
 *
 * @author CrowsOfWar
 */
public class RenderEarthspikes extends Render<EntityEarthspike> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/entity/earthspike.png");

    private ModelBase model;

    /**
     * @param renderManager
     */
    public RenderEarthspikes(RenderManager renderManager) {
        super(renderManager);
        this.model = new ModelEarthspikes();
    }

    @Override
    public void doRender(EntityEarthspike entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        GlStateManager.enableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);


        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
		GlStateManager.translate(0, -1.5, 0);

        model.render(entity, 0, 0, 0, 0, 0, 0.0625f);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEarthspike entity) {
        return TEXTURE;
    }
}



