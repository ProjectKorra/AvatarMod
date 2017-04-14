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
package com.crowsofwar.avatar.common.entity.mob;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonEatGrass extends EntityAIBase {
	
	private final EntitySkyBison bison;
	
	public EntityAiBisonEatGrass(EntitySkyBison bison) {
		this.bison = bison;
	}
	
	@Override
	public boolean shouldExecute() {
		return bison.wantsGrass();
	}
	
	@Override
	public void startExecuting() {
		
		System.out.println("Time to eat!!");
		
		World world = bison.worldObj;
		
		double maxDist = 2;
		
		double x = bison.posX + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		double z = bison.posX + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		
		int y = (int) bison.posY;
		while (world.isAirBlock(new BlockPos(x, y, z))) {
			y--;
		}
		
		bison.getMoveHelper().setMoveTo(x, y + 1, z, 1);
		System.out.println("Moving to " + x + ", " + (y + 1) + ", " + z);
		
	}
	
	@Override
	public boolean continueExecuting() {
		EntityMoveHelper mh = bison.getMoveHelper();
		if (bison.getDistanceSq(mh.getX(), mh.getY(), mh.getZ()) < 3 * 3) {
			System.out.println("Reached the ground");
			
			bison.eatGrassBonus();
			
		}
		return bison.wantsGrass();
	}
	
}
