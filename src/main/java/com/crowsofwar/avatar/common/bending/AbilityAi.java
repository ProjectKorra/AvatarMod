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
package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AbilityAi {
	
	private final BendingAbility ability;
	
	protected AbilityAi(BendingAbility ability) {
		this.ability = ability;
	}
	
	public void start(EntityLivingBase entity, Bender bender) {
		startExec(createCtx(entity, bender));
	}
	
	public void continueExec(EntityLivingBase entity, Bender bender) {
		continueExec(createCtx(entity, bender));
	}
	
	protected abstract void startExec(AbilityContext ctx);
	
	protected abstract boolean continueExec(AbilityContext ctx);
	
	private AbilityContext createCtx(EntityLivingBase entity, Bender bender) {
		return new AbilityContext(bender.getData(), entity, bender,
				Raytrace.getTargetBlock(entity, ability.getRaytrace()));
	}
	
}
