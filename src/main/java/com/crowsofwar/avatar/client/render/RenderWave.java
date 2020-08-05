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

import com.crowsofwar.avatar.common.bending.water.AbilityCreateWave;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.entity.EntityWave;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderWave extends RenderModel<EntityWave> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/wave.png");

	/**
	 * @param renderManager
	 */
	public RenderWave(RenderManager renderManager) {

		super(renderManager, new ModelWave());
	}

	@Override
	protected void performGlTransforms(EntityWave entity, double x, double y, double z, float
			entityYaw, float partialTicks) {

		GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
		GlStateManager.translate(0, -(entity.getWaveSize()/1.1 * 0.75), 0);
		if (entity.getAbility() instanceof AbilityCreateWave) {
			AbilityData data = AbilityData.get(entity.getOwner(), entity.getAbility().getName());
			if (data.getLevel() >= 1) {
				GlStateManager.scale(entity.getWaveSize()/2, (entity.getWaveSize()/2) * 0.75, entity.getWaveSize()/2);
			}
			else {
				GlStateManager.scale(entity.getWaveSize(), (entity.getWaveSize()) * 0.75, entity.getWaveSize());
			}
		}
		else {
			GlStateManager.scale(entity.getWaveSize() / 2, (entity.getWaveSize() / 2) * 0.75, entity.getWaveSize() / 2);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWave entity) {
		return TEXTURE;
	}

}

