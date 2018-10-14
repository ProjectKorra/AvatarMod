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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * @author CrowsOfWar
 */
public class RenderEarthspikes extends RenderModel<EntityEarthspike> {


	public static ResourceLocation TEXTURE;

	private ModelBase model;

	/**
	 * @param renderManager
	 */
	public RenderEarthspikes(RenderManager renderManager) {
		super(renderManager, new ModelEarthspikes());
		this.model = new ModelEarthspikes();
	}

	@Override
	public void doRender(EntityEarthspike entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {


		BlockPos below = entity.getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = entity.world.getBlockState(below).getBlock();
		if (belowBlock == Blocks.GRASS) {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike" + ".png");
		}
		else if (belowBlock == Blocks.DIRT) {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike_dirt" + ".png");
		}
	else 	if (belowBlock == Blocks.SAND) {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike_sand" + ".png");
		}
		else if (belowBlock == Blocks.SANDSTONE) {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike_sandstone" + ".png");
		}
		else if (belowBlock == Blocks.STONE) {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike_stone" + ".png");
		} else {
			TEXTURE = new ResourceLocation("avatarmod", "textures/entity/earthspike_stone" + ".png");
		}


		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);


		model.render(entity, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();

	}

	@Override
	protected void performGlTransforms(EntityEarthspike entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(entity.getSize(), entity.getSize(), entity.getSize());
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEarthspike entity) {
		return TEXTURE;
	}
}



