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

import com.crowsofwar.avatar.common.bending.AbilityContext;

import net.minecraft.entity.player.EntityPlayer;
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
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		
		EnumFacing facing = player.getHorizontalFacing();
		
		for (int i = 1; i <= 5; i++) {
			BlockPos pos = player.getPosition().offset(facing, i);
			world.destroyBlock(pos, true);
			pos = pos.up();
			world.destroyBlock(pos, true);
		}
		
		BlockPos pos = player.getPosition().offset(facing);
		world.setBlockToAir(pos);
		
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
