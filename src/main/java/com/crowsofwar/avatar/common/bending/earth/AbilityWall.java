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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbilityWall extends BendingAbility {
	
	public AbilityWall() {
		super(BendingType.EARTHBENDING, "earth_wall");
		requireRaytrace(6, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		EnumFacing cardinal = player.getHorizontalFacing();
		
		BlockPos lookPos = ctx.getClientLookBlock().toBlockPos();
		if (STATS_CONFIG.bendableBlocks.contains(world.getBlockState(lookPos).getBlock())) {
			System.out.println("BEND A WALL");
			EntityWall wall = new EntityWall(world);
			
			wall.setPosition(lookPos.getX() + .5, lookPos.getY(), lookPos.getZ() + .5);
			for (int i = 0; i < 5; i++) {
				
				int horizMod = -2 + i;
				int x = lookPos.getX()
						+ (cardinal == EnumFacing.NORTH || cardinal == EnumFacing.SOUTH ? horizMod : 0);
				int y = lookPos.getY() - 4;
				int z = lookPos.getZ()
						+ (cardinal == EnumFacing.EAST || cardinal == EnumFacing.WEST ? horizMod : 0);
				
				EntityWallSegment seg = new EntityWallSegment(world);
				seg.attachToWall(wall);
				seg.setPosition(x + .5, y, z + .5);
				seg.setDirection(cardinal);
				
				boolean foundAir = false, dontBreakMore = false;
				for (int j = EntityWallSegment.SEGMENT_HEIGHT - 1; j >= 0; j--) {
					BlockPos pos = new BlockPos(x, y + j, z);
					IBlockState state = world.getBlockState(pos);
					boolean bendable = STATS_CONFIG.bendableBlocks.contains(state.getBlock());
					if (!bendable || dontBreakMore) {
						state = Blocks.AIR.getDefaultState();
						dontBreakMore = true;
					}
					
					if (!foundAir && state.getBlock() == Blocks.AIR) {
						seg.setSize(.9f, 5 - j - 1);
						seg.setBlocksOffset(-(j + 1));
						seg.position().setY(y + j + 1);
						foundAir = true;
					}
					if (foundAir && state.getBlock() != Blocks.AIR) {
						// Extend bounding box
						seg.setSize(.9f, 5 - j);
						seg.setBlocksOffset(-j);
						seg.position().setY(y + j);
					}
					
					seg.setBlock(j, state);
					if (bendable && !dontBreakMore) world.setBlockToAir(pos);
				}
				
				world.spawnEntityInWorld(seg);
			}
			world.spawnEntityInWorld(wall);
		}
		
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
