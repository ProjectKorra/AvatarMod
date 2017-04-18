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
package com.crowsofwar.avatar.common.entity.ai;

import static net.minecraft.util.math.MathHelper.floor_double;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Bison lands when he is hungry. This allows the bison to eat grass and to
 * consume less food points. Considered a MOVEMENT task, so has mutex bits 1.
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonLand extends EntityAIBase {
	
	private final EntitySkyBison bison;
	
	public EntityAiBisonLand(EntitySkyBison bison) {
		this.bison = bison;
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		return bison.wantsGrass();
	}
	
	@Override
	public void startExecuting() {
		
		System.out.println("Trying to land");
		
		World world = bison.worldObj;
		
		int tries = 0;
		Vector landing;
		boolean isValidPosition;
		do {
			
			landing = findLandingPoint().add(0, 1, 0);
			tries++;
			
			Block block = world.getBlockState(landing.toBlockPos().down()).getBlock();
			isValidPosition = (block == Blocks.GRASS || block == Blocks.TALLGRASS) && canFit(landing);
			
			System.out.println("Landing pos " + landing + " --> on " + block + "; fits " + canFit(landing));
			
		} while (!isValidPosition && tries < 5);
		
		if (isValidPosition) {
			
			landing.add(0, 1, 0);
			bison.getMoveHelper().setMoveTo(landing.x(), landing.y() - 1, landing.z(), 1);
			System.out.println("Moving to " + landing);
			
		} else {
			System.out.println("can't find landing zone");
		}
		
	}
	
	@Override
	public boolean continueExecuting() {
		// Once got close to grass, close enough
		EntityMoveHelper mh = bison.getMoveHelper();
		if (bison.getDistanceSq(mh.getX(), mh.getY(), mh.getZ()) <= 5) {
			bison.getMoveHelper().action = Action.WAIT;
		}
		
		// Don't wander off until we have food!
		return !bison.isFull();
	}
	
	private Vector findLandingPoint() {
		
		double maxDist = 2;
		
		double x = bison.posX + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		double z = bison.posZ + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		
		int y = (int) bison.posY;
		while (!isSolidBlock(new BlockPos(x, y, z))) {
			y--;
		}
		return new Vector(x, y, z);
		
	}
	
	private boolean canFit(Vector pos) {
		
		double minX = pos.x() - bison.width / 2;
		double maxX = pos.x() + bison.width / 2;
		double minY = pos.y();
		double maxY = pos.y() + bison.height;
		double minZ = pos.z() - bison.width / 2;
		double maxZ = pos.z() + bison.width / 2;
		
		for (int x = floor_double(minX); x <= maxX; x++) {
			for (int y = floor_double(minY); y <= maxY; y++) {
				for (int z = floor_double(minZ); z <= maxZ; z++) {
					if (isSolidBlock(new BlockPos(x, y, z))) {
						return false;
					}
				}
			}
		}
		
		return true;
		
	}
	
	private boolean isSolidBlock(BlockPos pos) {
		World world = bison.worldObj;
		return world.isBlockNormalCube(pos, false);
	}
	
}
