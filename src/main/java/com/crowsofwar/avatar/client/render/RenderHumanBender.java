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

import com.crowsofwar.avatar.entity.mob.EntityHumanBender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderHumanBender extends RenderLiving<EntityHumanBender> {

	private final ResourceLocation[] locations;

	/**
	 * @param renderManager
	 * @param texture       Name of the texture file to be used, without the ending
	 *                      "_#.png". E.g. "airbender"
	 */
	public RenderHumanBender(RenderManager renderManager, String texture, int textures) {
		super(renderManager, new ModelBiped(0, 0, 64, 64), 0.5f);

		locations = new ResourceLocation[textures];
		for (int i = 0; i < textures; i++) {
			locations[i] = new ResourceLocation("avatarmod", "textures/mob/" + texture + "_" + i + ".png");
		}

	}

	@Override
	public void doRender(EntityHumanBender entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHumanBender entity) {
		int location = Math.min(locations.length - 1, entity.getSkin());
		return locations[location];
	}

}
