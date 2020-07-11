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
package com.crowsofwar.avatar.entity.ai;

import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonWander extends EntityAIBase {

	private final EntitySkyBison entity;

	public EntityAiBisonWander(EntitySkyBison entity) {
		this.entity = entity;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {

		if (entity.isSitting()) return false;
		if (entity.getControllingPassenger() != null) return false;
		if (entity.wantsGrass()) return false;

		EntityMoveHelper moveHelper = entity.getMoveHelper();

		if (!moveHelper.isUpdating()) {
			return true;
		} else {
			double dx = moveHelper.getX() - this.entity.posX;
			double dy = moveHelper.getY() - this.entity.posY;
			double dz = moveHelper.getZ() - this.entity.posZ;
			double distToTargetSq = dx * dx + dy * dy + dz * dz;
			return distToTargetSq < 1.0D || distToTargetSq > 3600.0D;
		}

	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		Random random = entity.getRNG();
		EntityPlayer owner = entity.getOwner();
		Vector centerPoint;
		if (owner != null) {
			centerPoint = Vector.getEntityPos(owner);
		} else {
			centerPoint = entity.getOriginalPos();
		}

		double x = centerPoint.x() + (random.nextFloat() * 2 - 1) * 32;
		double y = centerPoint.y() + (random.nextFloat() * 2 - 1) * 32;
		double z = centerPoint.z() + (random.nextFloat() * 2 - 1) * 32;

		this.entity.getMoveHelper().setMoveTo(x, y, z, 1.0D);
	}

}
