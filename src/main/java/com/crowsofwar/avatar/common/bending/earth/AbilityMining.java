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
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
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
			
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			
			AbilityData abilityData = ctx.getAbilityData();
			float xp = abilityData.getTotalXp();
			
			ctx.getData().getAbilityData(this).addXp(SKILLS_CONFIG.miningUse);
			
			//@formatter:off
			// 0 = S 0x +z    1 = SW -x +z
			// 2 = W -x 0z    3 = NW -x -z
			// 4 = N 0x -z    5 = NE +x -z
			// 6 = E +x 0z    7 = SE +x +z
			//@formatter:on
			
			int yaw = (int) floor((entity.rotationYaw * 8 / 360) + 0.5) & 7;
			int x = 0, z = 0;
			if (yaw == 1 || yaw == 2 || yaw == 3) x = -1;
			if (yaw == 5 || yaw == 6 || yaw == 7) x = 1;
			if (yaw == 3 || yaw == 4 || yaw == 5) z = -1;
			if (yaw == 0 || yaw == 1 || yaw == 7) z = 1;
			
			// Pitch: 0=forward, +1=90deg up, etc
			// Use abs and post-mul to fix weirdness with negatives
			
			int pitch = (int) floor((abs(entity.rotationPitch) * 8 / 360) + 0.5) & 7;
			pitch *= -abs(entity.rotationPitch) / entity.rotationPitch;
			
			// Each starting position of the ray to mine out
			List<VectorI> rays = new ArrayList<>();
			rays.add(new VectorI(entity.getPosition()));
			rays.add(new VectorI(entity.getPosition().up()));
			
			// If yaw is diagonal; SW, NW, NE, SE
			if (yaw % 2 == 1) {
				rays.add(new VectorI(entity.getPosition().east()));
				rays.add(new VectorI(entity.getPosition().east().up()));
			}
			// Add height to excavating a stairway so you don't bump your head
			if (pitch != 0) {
				rays.add(new VectorI(entity.getPosition().up(2)));
			}
			
			VectorI dir = new VectorI(x, pitch, z);
			if (abs(pitch) == 2) {
				dir.setX(0);
				dir.setZ(0);
				dir.setY(abs(pitch) / pitch);
				rays.clear();
				rays.add(new VectorI(entity.getPosition().up(pitch < 0 ? 0 : 1)));
			}
			
			int dist = getDistance(abilityData.getLevel(), abilityData.getPath());
			int fortune = getFortune(abilityData.getLevel(), abilityData.getPath());
			
			// For keeping track of already inspected/to-be-inspected positions
			// of ore blocks
			// orePos: Position that needs to be inspected
			// inspectedOrePos: Position that was already inspected (since the
			// queue items will be deleted)
			Queue<BlockPos> orePos = new LinkedList<>();
			Set<BlockPos> inspectedOrePos = new HashSet<>();
			
			for (VectorI ray : rays) {
				
				for (int i = 1; i <= dist; i++) {
					
					BlockPos pos = ray.plus(dir.times(i)).toBlockPos();
					Block block = world.getBlockState(pos).getBlock();
					
					if (block instanceof BlockOre || block instanceof BlockRedstoneOre) {
						orePos.add(pos);
						inspectedOrePos.add(pos);
					}
					
					// Stop at non-bendable blocks
					if (!breakBlock(pos, block, ctx, i, fortune) && block != Blocks.AIR) {
						break;
					}
					
				}
				
			}
			
			// mine nearby ores as well
			// flood-fill search
			if (abilityData.getPath() == SECOND) {
				int i = 0;
				while (!orePos.isEmpty()) {
					
					BlockPos pos = orePos.poll();
					System.out.println("Inspecting " + pos);
					
					for (int j = 0; j < 6; j++) {
						EnumFacing facing = EnumFacing.values()[j];
						BlockPos inspectingPos = pos.offset(facing);
						Block inspectingBlock = world.getBlockState(pos).getBlock();
						
						if (inspectingBlock instanceof BlockOre
								|| inspectingBlock instanceof BlockRedstoneOre) {
							
							System.out.println(" -> Found another ore at " + inspectingPos);
							System.out.println("    it is: " + inspectingBlock);
							
							breakBlock(inspectingPos, inspectingBlock, ctx, i * 5 + 20, fortune);
							
							boolean alreadyInspected = orePos.contains(inspectingPos)
									|| inspectedOrePos.contains(inspectingPos);
							if (!alreadyInspected) {
								
								orePos.add(inspectingPos);
								inspectedOrePos.add(inspectingPos);
								
								System.out.println("    Marking for further investigation");
								
							}
							
						}
						
					}
					
				}
			}
			
		}
		
	}
	
	/**
	 * Calculates a random distance to mine blocks based on the current level.
	 */
	private int getDistance(int level, AbilityTreePath path) {
		
		int min, max;
		if (level == 3 && path == FIRST) {
			
			min = 5;
			max = 6;
			
		} else if (level == 3) {
			
			min = 4;
			max = 6;
			
		} else if (level == 2) {
			
			min = 3;
			max = 5;
			
		} else if (level == 1) {
			
			min = 2;
			max = 4;
			
		} else {
			
			min = 2;
			max = 3;
			
		}
		
		return (int) (min + Math.random() * (max - min));
		
	}
	
	private int getFortune(int level, AbilityTreePath path) {
		if (level == 3) {
			return path == SECOND ? 3 : 2;
		} else if (level == 2) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Breaks the block at the specified position, but doesn't break
	 * non-bendable blocks. Returns false if not able to break (since the block
	 * isn't bendable).
	 */
	private boolean breakBlock(BlockPos pos, Block block, AbilityContext ctx, int i, int fortune) {
		
		AvatarWorldData wd = AvatarWorldData.getDataFromWorld(ctx.getWorld());
		
		boolean bendable = STATS_CONFIG.bendableBlocks.contains(block);
		if (bendable) {
			
			boolean drop = !ctx.getBender().isCreativeMode();
			wd.getScheduledDestroyBlocks().add(wd.new ScheduledDestroyBlock(pos, i * 3, drop, fortune));
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
}
