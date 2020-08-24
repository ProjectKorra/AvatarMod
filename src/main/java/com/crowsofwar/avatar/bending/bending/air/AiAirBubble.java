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
package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityAirBubble;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import net.minecraft.entity.EntityLiving;

import static com.crowsofwar.avatar.bending.bending.BendingAi.AbilityType.DEFENSIVE;
import static com.crowsofwar.avatar.util.data.StatusControlController.BUBBLE_CONTRACT;
import static com.crowsofwar.avatar.util.data.StatusControlController.BUBBLE_EXPAND;

/**
 * @author CrowsOfWar
 */
public class AiAirBubble extends BendingAi {

    /**
     * @param ability
     * @param entity
     * @param bender
     */
    protected AiAirBubble(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
        setMutexBits(3);
    }

    @Override
    protected boolean shouldExec() {
        return entity.getAttackTarget() != null && entity.world.rand.nextBoolean() && entity.getHealth() < entity.getMaxHealth() * 0.75F;
    }

    @Override
    protected void startExec() {

    }


    @Override
    public float getMaxTargetRange() {
        return 7;
    }

    @Override
    public float getMinTargetRange() {
        return 0;
    }

    @Override
    public int getWaitDuration() {
        return 2;
    }

    @Override
    public int getTotalDuration() {
        return 80;
    }

    @Override
    public boolean shouldExecAbility() {
        return timeExecuting >= getWaitDuration();
    }

    @Override
    public AbilityType[] getAbilityTypes() {
        return new AbilityType[]{
                DEFENSIVE
        };
    }

    @Override
    public StatusControl[] getStatusControls() {
        return new StatusControl[]{
                BUBBLE_EXPAND,
                BUBBLE_CONTRACT
        };
    }

    @Override
    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        EntityAirBubble airBubble = AvatarEntity.lookupOwnedEntity(entity.world, EntityAirBubble.class, entity);

        if (timeExecuting >= 200)
            return true;
        else if (airBubble != null && airBubble.getHealth() < 1)
            return true;
        else if (entity.getAttackTarget() != null && entity.getDistance(entity.getAttackTarget()) < 2 && entity.world.rand.nextBoolean() && timeExecuting >= 20)
            return true;

        return super.shouldExecStatCtrl(statusControl);
    }
}
