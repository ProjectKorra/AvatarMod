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

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiWanderFly extends EntityAIBase {
	
	private final EntityCreature entity;
	
	public EntityAiWanderFly(EntityCreature entity) {
		this.entity = entity;
		this.setMutexBits(1);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityMoveHelper entitymovehelper = this.entity.getMoveHelper();
		
		if (!entitymovehelper.isUpdating()) {
			return true;
		} else {
			double d0 = entitymovehelper.getX() - this.entity.posX;
			double d1 = entitymovehelper.getY() - this.entity.posY;
			double d2 = entitymovehelper.getZ() - this.entity.posZ;
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			return d3 < 1.0D || d3 > 3600.0D;
		}
	}
	
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting() {
		return false;
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		Random random = this.entity.getRNG();
		double d0 = this.entity.posX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
		double d1 = this.entity.posY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
		double d2 = this.entity.posZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
		this.entity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
	}
	
}
