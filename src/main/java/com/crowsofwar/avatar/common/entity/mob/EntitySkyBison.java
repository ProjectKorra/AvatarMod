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

import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

/**
 * EntityGhast
 * 
 * @author CrowsOfWar
 */
public class EntitySkyBison extends EntityBender {
	
	/**
	 * @param world
	 */
	public EntitySkyBison(World world) {
		super(world);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		
		this.tasks.addTask(1, BendingAbility.ABILITY_AIR_BUBBLE.getAi(this, this));
		this.tasks.addTask(2, BendingAbility.ABILITY_AIR_GUST.getAi(this, this));
		this.tasks.addTask(3, BendingAbility.ABILITY_AIRBLADE.getAi(this, this));
		
		this.tasks.addTask(6, new EntityAiWanderFly(this));
		
	}
	
}
