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
package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityMining extends EarthAbility {
	
	public AbilityMining() {
		super("mine_blocks");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		if (ctx.consumeChi(STATS_CONFIG.chiMining)) {
			
			EntityPlayer player = ctx.getPlayerEntity();
			World world = ctx.getWorld();
			
			int chanceMin, chanceMax;
			float xp = ctx.getData().getAbilityData(this).getTotalXp();
			if (xp == 100) {
				chanceMin = 5;
				chanceMax = 6;
			} else if (xp >= 75) {
				chanceMin = 4;
				chanceMax = 6;
			} else if (xp >= 50) {
				chanceMin = 3;
				chanceMax = 5;
			} else if (xp >= 25) {
				chanceMin = 2;
				chanceMax = 4;
			} else {
				chanceMin = 2;
				chanceMax = 3;
			}
			int dist = chanceMin + (int) Math.round(Math.random() * (chanceMax - chanceMin));
			ctx.getData().getAbilityData(this).addXp(SKILLS_CONFIG.miningUse);
			
		//@formatter:off
		// 0 = S 0x +z    1 = SW -x +z
		// 2 = W -x 0z    3 = NW -x -z
		// 4 = N 0x -z    5 = NE +x -z
		// 6 = E +x 0z    7 = SE +x +z
		//@formatter:on
			int yaw = (int) floor((player.rotationYaw * 8 / 360) + 0.5) & 7;
			int x = 0, z = 0;
			if (yaw == 1 || yaw == 2 || yaw == 3) x = -1;
			if (yaw == 5 || yaw == 6 || yaw == 7) x = 1;
			if (yaw == 3 || yaw == 4 || yaw == 5) z = -1;
			if (yaw == 0 || yaw == 1 || yaw == 7) z = 1;
			
			// Pitch: 0 = forward, 1 = 45 deg up, 2 = 90 deg up
			// -1 = 45 deg down, -2 = 90 deg down
			// Use abs and post-mul to fix weirdness with negatives... not
			// totally
			// investigated
			int pitch = (int) floor((abs(player.rotationPitch) * 8 / 360) + 0.5) & 7;
			pitch *= -abs(player.rotationPitch) / player.rotationPitch;
			
			// Each starting position of the ray to mine out
			List<VectorI> rays = new ArrayList<>();
			rays.add(new VectorI(player.getPosition()));
			rays.add(new VectorI(player.getPosition().up()));
			
			// If yaw is diagonal; SW, NW, NE, SE
			if (yaw % 2 == 1) {
				rays.add(new VectorI(player.getPosition().east()));
				rays.add(new VectorI(player.getPosition().east().up()));
			}
			// Add height to excavating a stairway so you don't bump your head
			if (pitch != 0) {
				rays.add(new VectorI(player.getPosition().up(2)));
			}
			
			VectorI dir = new VectorI(x, pitch, z);
			if (abs(pitch) == 2) {
				dir.setX(0);
				dir.setZ(0);
				dir.setY(abs(pitch) / pitch);
				rays.clear();
				rays.add(new VectorI(player.getPosition().up(pitch < 0 ? 0 : 1)));
			}
			
			for (VectorI ray : rays) {
				for (int i = 1; i <= dist; i++) {
					BlockPos pos = ray.plus(dir.times(i)).toBlockPos();
					Block block = world.getBlockState(pos).getBlock();
					
					boolean bendable = STATS_CONFIG.bendableBlocks.contains(block);
					if (bendable) {
						AvatarWorldData wd = AvatarWorldData.getDataFromWorld(world);
						wd.getScheduledDestroyBlocks().add(wd.new ScheduledDestroyBlock(pos, i * 3,
								!player.capabilities.isCreativeMode));
					} else if (block != Blocks.AIR) {
						break;
					}
				}
			}
			
		}
		
	}
	
}
