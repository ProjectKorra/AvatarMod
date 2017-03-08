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

package com.crowsofwar.avatar.common.bending.fire;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.data.AbilityContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityLightFire extends FireAbility {
	
	private final ParticleSpawner particles;
	
	/**
	 * @param controller
	 */
	public AbilityLightFire() {
		super("light_fire");
		requireRaytrace(-1, false);
		particles = new NetworkParticleSpawner();
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		World world = ctx.getWorld();
		
		VectorI looking = ctx.verifyClientLookBlock(-1, 5);
		EnumFacing side = ctx.getLookSide();
		if (ctx.isLookingAtBlock(-1, 5)) {
			VectorI setAt = new VectorI(looking.x(), looking.y(), looking.z());
			setAt.offset(side);
			BlockPos blockPos = setAt.toBlockPos();
			
			if (world.isRainingAt(blockPos)) {
				
				particles.spawnParticles(world, ParticleType.CLOUD, 3, 7, ctx.getLookPos(),
						new Vector(0.5f, 0.75f, 0.5f));
				
				world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
						SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS,
						0.4f + (float) Math.random() * 0.2f, 0.9f + (float) Math.random() * 0.2f);
				
			} else if (world.getBlockState(blockPos).getBlock() == Blocks.AIR
					&& Blocks.FIRE.canPlaceBlockAt(world, blockPos)) {
				
				if (ctx.consumeChi(STATS_CONFIG.chiLightFire)) {
					
					world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
					world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
							SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS,
							0.7f + (float) Math.random() * 0.3f, 0.9f + (float) Math.random() * 0.2f);
					
				}
				
			}
		}
	}
	
}
