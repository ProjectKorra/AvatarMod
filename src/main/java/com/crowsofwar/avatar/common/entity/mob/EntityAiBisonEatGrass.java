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

import static net.minecraft.util.math.MathHelper.floor_double;

import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonEatGrass extends EntityAIBase {
	
	private final EntitySkyBison bison;
	private int eatGrassCountdown;
	
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
		eatGrassCountdown = 30;
		
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
			bison.getMoveHelper().setMoveTo(landing.x(), landing.y(), landing.z(), 1);
			System.out.println("Moving to " + landing);
			
		} else {
			System.out.println("can't find landing zone");
		}
		
	}
	
	@Override
	public boolean continueExecuting() {
		World world = bison.worldObj;
		EntityMoveHelper mh = bison.getMoveHelper();
		
		BlockPos downPos = bison.getPosition().down();
		boolean reachedGround = world.isSideSolid(downPos, EnumFacing.UP);
		if (reachedGround) {
			System.out.println("Reached the ground");
			
			tryEatGrass();
			
		}
		return bison.wantsGrass();
	}
	
	private Vector findLandingPoint() {
		
		double maxDist = 2;
		
		double x = bison.posX + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		double z = bison.posZ + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		
		int y = (int) bison.posY;
		while (bison.worldObj.isAirBlock(new BlockPos(x, y, z))) {
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
					if (!bison.worldObj.isAirBlock(new BlockPos(x, y, z))) {
						return false;
					}
				}
			}
		}
		
		return true;
		
	}
	
	private void tryEatGrass() {
		eatGrassCountdown--;
		if (eatGrassCountdown <= 0) {
			eatGrassCountdown = 30;
			
			BlockPos downPos = bison.getPosition().down();
			World world = bison.worldObj;
			
			boolean mobGriefing = world.getGameRules().getBoolean("mobGriefing");
			
			BlockPos ediblePos = null;
			
			Block block = world.getBlockState(downPos).getBlock();
			if (block == Blocks.GRASS) {
				ediblePos = downPos;
			} else {
				block = world.getBlockState(downPos.up()).getBlock();
				if (block == Blocks.TALLGRASS || block == Blocks.YELLOW_FLOWER
						|| block == Blocks.RED_FLOWER) {
					
					ediblePos = downPos.up();
					
				}
			}
			
			if (ediblePos != null) {
				
				if (mobGriefing) {
					world.playEvent(2001, ediblePos, Block.getIdFromBlock(Blocks.GRASS));
					world.setBlockState(ediblePos, Blocks.DIRT.getDefaultState(), 2);
				}
				
				bison.eatGrassBonus();
				
			}
			
		}
	}
	
}
