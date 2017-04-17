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

import com.crowsofwar.avatar.common.entity.data.AnimalCondition;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.ai.EntityAIBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiBisonBreeding extends EntityAIBase {
	
	private final EntitySkyBison bison;
	
	public EntityAiBisonBreeding(EntitySkyBison bison) {
		this.bison = bison;
	}
	
	@Override
	public boolean shouldExecute() {
		AnimalCondition cond = bison.getCondition();
		return !cond.isSterile() && cond.getBreedTimer() == 0;
	}
	
	@Override
	public void startExecuting() {
		System.out.println("in love");
		bison.setInLove(true);
	}
	
	@Override
	public boolean continueExecuting() {
		AnimalCondition cond = bison.getCondition();
		
		if (!shouldExecute()) {
			bison.setInLove(false);
		}
		return shouldExecute();
	}
	
}
