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

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderSkyBison extends RenderLiving<EntitySkyBison> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/mob/flyingbison.png");

	/**
	 * @param rm (RenderManager)
	 */
	public RenderSkyBison(RenderManager rm) {
		super(rm, new ModelFlyingBison(), 0);
		// shadowSize not important; adjusted based on size below
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkyBison entity) {
		return TEXTURE;
	}

	@Override
	public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw,
									  float partialTicks) {

		//TODO: Centralise render update code
		EntitySkyBison bison = (EntitySkyBison) entity;
		shadowSize = 2.5f * bison.getCondition().getSizeMultiplier();
		super.doRenderShadowAndFire(entity, x, y, z, yaw, partialTicks);

	}
}
