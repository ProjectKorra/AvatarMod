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

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

/**
 * @author CrowsOfWar
 */
public class EntityAiKeepDistance extends EntityAIBase {

	private final EntityCreature entity;
	private final double maxSafeDistance;
	private final double speed;
	private Path path;

	public EntityAiKeepDistance(EntityCreature entity, double maxSafeDistance, double speed) {
		this.entity = entity;
		this.maxSafeDistance = maxSafeDistance;
		this.speed = speed;
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = entity.getAttackTarget();
		if (target != null && entity.getDistanceSq(target) <= maxSafeDistance * maxSafeDistance) {

			Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7,
					new Vec3d(target.posX, target.posY, target.posZ));

			if (vec3d == null) {
				return false;
			} else if (entity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < entity
					.getDistanceSq(target)) {
				return false;
			} else {
				path = entity.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
				return path != null;
			}

		} else {
			return false;
		}
	}

	@Override
	public void startExecuting() {
		entity.getNavigator().setPath(path, speed);
	}

}
