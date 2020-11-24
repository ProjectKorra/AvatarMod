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

import com.crowsofwar.avatar.entity.EntityIceShard;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderIceShard extends RenderModel<EntityIceShard> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/ice-shard.png");

	public RenderIceShard(RenderManager renderManager) {
		super(renderManager, new ModelIceShard());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityIceShard entity) {
		return TEXTURE;
	}

	@Override
	protected void performGlTransforms(EntityIceShard entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// Should be rotating in degrees here...?
		// radians doesn't work
		GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
	}
}
