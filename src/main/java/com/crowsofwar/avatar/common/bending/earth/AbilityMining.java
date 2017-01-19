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
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		// EnumFacing facing = player.getHorizontalFacing();
		
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
		
		// Each starting position of the ray to mine out
		List<VectorI> rays = new ArrayList<>();
		rays.add(new VectorI(player.getPosition()));
		
		// If yaw is diagonal; SW, NW, NE, SE
		if (yaw % 2 == 1) {
			rays.add(new VectorI(player.getPosition().east()));
		}
		
		VectorI dir = new VectorI(x, 0, z);
		
		for (VectorI ray : rays) {
			for (int y = 0; y <= 1; y++) {
				for (int i = 1; i <= 5; i++) {
					BlockPos pos = ray.plus(dir.times(i)).plus(0, y, 0).toBlockPos();
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
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
