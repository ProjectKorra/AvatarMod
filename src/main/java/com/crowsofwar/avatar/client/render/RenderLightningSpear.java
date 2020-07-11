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

import com.crowsofwar.avatar.entity.EntityLightningSpear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderLightningSpear extends RenderModel<EntityLightningSpear> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/lightning_spear.png");

	/**
	 * @param renderManager
	 */
	public RenderLightningSpear(RenderManager renderManager) {
		super(renderManager, new ModelLightningSpear());
		setGlowing();
	}

	@Override
	protected void performGlTransforms(EntityLightningSpear entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// Should be rotating in degrees here...?
		// radians doesn't work
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
		GlStateManager.scale(entity.getWidth(), entity.getHeight(), entity.getWidth());
		GlStateManager.translate(0, entity.getHeight() / 2, 0);
		GlStateManager.rotate((float) (entity.ticksExisted / 20.0 * entity.getDegreesPerSecond()), 0, 0, 1);

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLightningSpear entity) {
		return TEXTURE;
	}

}
