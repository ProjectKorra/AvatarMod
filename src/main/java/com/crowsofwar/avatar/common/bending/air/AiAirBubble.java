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
package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;

import net.minecraft.entity.EntityLiving;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AiAirBubble extends BendingAi {
	
	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiAirBubble(BendingAbility ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}
	
	@Override
	protected void startExec() {
		execAbility();
	}
	
	@Override
	public boolean shouldExecute() {
		
		boolean underAttack = entity.getCombatTracker().getCombatDuration() <= 100 || true;
		boolean already = AvatarEntity.lookupEntity(entity.worldObj, EntityAirBubble.class,
				bubble -> bubble.getOwner() == entity) != null;
		boolean lowHealth = entity.getHealth() / entity.getMaxHealth() <= 0.25f || entity.getHealth() < 10;
		
		System.out.println(underAttack + "/ " + already + "/" + lowHealth);
		
		return !already && underAttack && lowHealth;
		
	}
	
}
