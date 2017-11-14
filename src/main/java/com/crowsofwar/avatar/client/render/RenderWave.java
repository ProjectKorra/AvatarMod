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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;

import com.crowsofwar.avatar.common.entity.EntityWave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderWave extends Render<EntityWave> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/wave.png");

	private ModelBase model;

	/**
	 * @param renderManager
	 */
	public RenderWave(RenderManager renderManager) {
		super(renderManager);
		this.model = new ModelWave();
	}

	@Override
	public void doRender(EntityWave entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {


		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);


		GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);

		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);

		model.render(entity, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWave entity) {
		return TEXTURE;
	}
}

