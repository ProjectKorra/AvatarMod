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
 * Represents behavior needed for activation of an ability by a mob/AI. Before
 * most abilities are activated, some sort of preparation is required; for
 * example, air gust requires the user to aim at an enemy entity. This class
 * wraps all the preparatory behavior so the ability can be activated at the
 * appropriate time.
 * <p>
 * There is one instance of AbilityAi per ability. To start execution, call
 * {@link #start(EntityLivingBase, Bender) start}. {@link #isContinuous() Some
 * abilities} will require more calls to
 * {@link #continueExec(EntityLivingBase, Bender) continueExec}. Internally,
 * each AI sets the appropriate state of the entity, then calls
 * {@link BendingAbility#execute(AbilityContext)}.
 * 
 * @author CrowsOfWar
 */
public abstract class AbilityAi {
	
	private final BendingAbility ability;
	
	protected AbilityAi(BendingAbility ability) {
		this.ability = ability;
	}
	
	/**
	 * Start to execute this ability. If {@link #isContinuous()}, call
	 * {@link #continueExec(EntityLivingBase, Bender) continueExec} afterwards.
	 */
	public void start(EntityLivingBase entity, Bender bender) {
		startExec(createCtx(entity, bender));
	}
	
	protected abstract void startExec(AbilityContext ctx);
	
	/**
	 * Continues executing this ability. Returns whether to keep calling
	 * {@link #continueExec(EntityLivingBase, Bender) continueExec}. Only
	 * necessary to call if {@link #isContinuous()} returns true.
	 */
	public boolean continueExec(EntityLivingBase entity, Bender bender) {
		return false;
	}
	
	protected AbilityContext createCtx(EntityLivingBase entity, Bender bender) {
		return new AbilityContext(bender.getData(), entity, bender,
				Raytrace.getTargetBlock(entity, ability.getRaytrace()));
	}
	
	/**
	 * Returns whether this ability requires calls to
	 * {@link #continueExec(EntityLivingBase, Bender) continueExec} after
	 * initially calling {@link #start(EntityLivingBase, Bender) start}.
	 */
	public boolean isContinuous() {
		return false;
	}
	
	protected void execAbility(AbilityContext ctx) {
		ability.execute(ctx);
	}
	
}
