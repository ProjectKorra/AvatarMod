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
package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.avatar.common.bending.BendingType.WATERBENDING;
import static com.crowsofwar.avatar.common.util.AvatarUtils.afterVelocityAdded;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityWaterSkate extends BendingAbility {
	
	public AbilityWaterSkate() {
		super(WATERBENDING, "water_skate");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		World world = ctx.getWorld();
		
		Vector look = Vector.toRectangular(Math.toRadians(player.rotationYaw), 0);
		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(player).add(0, -1, 0),
				look, 2, (pos, blockState) -> blockState.getBlock() == Blocks.WATER);
		if (result.hitSomething()) {
			
			VectorI pos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(pos.toBlockPos());
			IBlockState up = world.getBlockState(pos.toBlockPos().up());
			
			for (int i = 0; i < 3; i++) {
				if (world.getBlockState(pos.toBlockPos().up()).getBlock() == Blocks.AIR) {
					player.setPosition(pos.x() + .5, pos.y() + 1.2, pos.z() + .5);
					data.setSkateTime(20);
					
					afterVelocityAdded(player);
					
					break;
				}
				pos.add(0, 1, 0);
			}
			
		}
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}
	
}
