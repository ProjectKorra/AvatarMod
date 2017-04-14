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

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Bison eats grass when on ground. Designed to work with BisonLand AI so
 * BisonLand causes bison to land when hungry, then this makes the bison eat
 * grass. Since it is an action(non-movement) task the mutex bits are 0.
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonEatGrass extends EntityAIBase {
	
	private final EntitySkyBison bison;
	
	/**
	 * Keeps track of eating grass state. -1 = not eating grass. Starts at 30
	 * and decrements until 0, at which point grass is eaten and cycle repeats.
	 */
	private int eatGrassCountdown;
	
	/**
	 * When not eating grass, is -1. Then increments every tick that the bison
	 * has been eating grass.
	 */
	private int eatGrassTime;
	
	public EntityAiBisonEatGrass(EntitySkyBison bison) {
		this.bison = bison;
		eatGrassCountdown = -1;
		eatGrassTime = -1;
		
		setMutexBits(0);
	}
	
	public boolean isEatingGrass() {
		return eatGrassCountdown > -1;
	}
	
	public int getEatGrassTime() {
		return eatGrassTime;
	}
	
	@Override
	public boolean shouldExecute() {
		return bison.wantsGrass() && isOnGround();
	}
	
	@Override
	public void startExecuting() {
		
		System.out.println("Time to eat!!");
		continueExecuting();
		
	}
	
	@Override
	public boolean continueExecuting() {
		
		boolean keepExecuting = bison.wantsGrass() && isOnGround();
		System.out.println("keepExecuting " + eatGrassTime);
		
		World world = bison.worldObj;
		EntityMoveHelper mh = bison.getMoveHelper();
		
		if (!isEatingGrass()) {
			// Just reached ground
			eatGrassCountdown = 30;
			eatGrassTime = 0;
			bison.getMoveHelper().setMoveTo(bison.posX, bison.posY, bison.posZ, 1);
		}
		tryEatGrass();
		
		if (eatGrassTime > 80) {
			keepExecuting = false;
		}
		
		if (!keepExecuting) {
			eatGrassCountdown = -1;
			eatGrassTime = -1;
		}
		
		return keepExecuting;
		
	}
	
	private void tryEatGrass() {
		eatGrassTime++;
		eatGrassCountdown--;
		if (eatGrassCountdown <= 0) {
			eatGrassCountdown = 30;
			
			System.out.println("Try some grass");
			
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
					System.out.println("Set to dirt");
					world.playEvent(2001, ediblePos, Block.getIdFromBlock(Blocks.GRASS));
					if (block == Blocks.GRASS) {
						world.setBlockState(ediblePos, Blocks.DIRT.getDefaultState(), 2);
					}
				}
				
				bison.eatGrassBonus();
				
				System.out.println("Ate at " + ediblePos);
				
			}
			
		}
	}
	
	private boolean isSolidBlock(BlockPos pos) {
		World world = bison.worldObj;
		return world.isBlockNormalCube(pos, false);
	}
	
	private boolean isOnGround() {
		BlockPos downPos = bison.getPosition().down();
		return isSolidBlock(downPos);
	}
	
}
