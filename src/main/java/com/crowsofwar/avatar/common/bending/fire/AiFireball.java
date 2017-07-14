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
package com.crowsofwar.avatar.common.bending.fire;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AiFireball extends BendingAi {
	
	private int timeExecuting;
	
	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiFireball(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		timeExecuting = 0;
		setMutexBits(2);
	}
	
	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		data.chi().setMaxChi(10);
		data.chi().setTotalChi(10);
		data.chi().setAvailableChi(10);
		execAbility();
		data.setAbilityCooldown(100);
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		
		if (entity.getAttackTarget() == null) return false;
		
		Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
		entity.rotationYaw = (float) toDegrees(rotations.y());
		entity.rotationPitch = (float) toDegrees(rotations.x());
		
		if (timeExecuting >= 40) {
			BendingData data = bender.getData();
			execStatusControl(StatusControl.THROW_FIREBALL);
			timeExecuting = 0;
			return false;
		} else {
			return true;
		}
		
	}
	
	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSqToEntity(target) > 4 * 4
				&& bender.getData().getAbilityCooldown() == 0 && entity.getRNG().nextBoolean();
	}
	
	@Override
	public void updateTask() {
		timeExecuting++;
	}
	
	@Override
	public void resetTask() {
		
		EntityFireball fireball = AvatarEntity.lookupEntity(entity.world, EntityFireball.class, //
				fire -> fire.getBehavior() instanceof FireballBehavior.PlayerControlled
						&& fire.getOwner() == entity);
		
		if (fireball != null) {
			fireball.setDead();
			bender.getData().removeStatusControl(StatusControl.THROW_FIREBALL);
		}
		
	}
	
}
