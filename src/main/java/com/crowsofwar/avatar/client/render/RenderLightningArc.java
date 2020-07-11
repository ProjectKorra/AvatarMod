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

import com.crowsofwar.avatar.entity.EntityLightningArc;
import com.crowsofwar.avatar.client.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderLightningArc extends RenderArc {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/lightning-ribbon.png");

	private final ParticleSpawner particleSpawner;

	public RenderLightningArc(RenderManager renderManager) {
		super(renderManager, false);
		enableFullBrightness();
		particleSpawner = new ClientParticleSpawner();
	}

	@Override
	public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
						 float partialTicks) {

		EntityLightningArc arc = (EntityLightningArc) entity;
		renderArc(arc, partialTicks, 0.8f, 0.67f * arc.getSizeMultiplier());
		renderArc(arc, partialTicks, 0.3f, 2 * arc.getSizeMultiplier());

	}

	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

}
