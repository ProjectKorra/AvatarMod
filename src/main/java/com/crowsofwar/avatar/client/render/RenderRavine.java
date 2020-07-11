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

import com.crowsofwar.avatar.entity.EntityRavine;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class RenderRavine extends Render<EntityRavine> {

	private final Random random;

	/**
	 * @param renderManager
	 */
	public RenderRavine(RenderManager renderManager) {
		super(renderManager);
		this.random = new Random();
	}

	@Override
	public void doRender(EntityRavine entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {
		World world = entity.world;
		IBlockState blockState = world.getBlockState(entity.getPosition().down());
		if (blockState.getBlock() != Blocks.AIR)
			for (int i = 0; i < 3; i++)
				world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX + world.rand.nextGaussian() * 0.5,
						entity.posY + world.rand.nextDouble(), entity.posZ + world.rand.nextGaussian() * 0.5,
						random.nextGaussian() * 0.4, random.nextDouble() * 0.4, random.nextGaussian() * 0.4,
						Block.getStateId(blockState));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRavine entity) {
		return null;
	}

}
