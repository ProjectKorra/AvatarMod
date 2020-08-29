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
package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;

/**
 * @author CrowsOfWar
 */
public class AiFireball extends BendingAi {


    /**
     * @param ability The Ability
     * @param entity  The entity
     * @param bender  The bender, from the entity
     */
    protected AiFireball(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
        setMutexBits(3);
    }

    @Override
    protected void startExec() {
        execAbility();
    }


    @Override
    protected boolean shouldExec() {
        EntityLivingBase target = entity.getAttackTarget();
        return target != null;
    }

    @Override
    public void resetTask() {
        super.resetTask();

        for (int i = 0; i < 3; i++) {
            EntityFireball fireball = AvatarEntity.lookupEntity(entity.world, EntityFireball.class, //
                    fire -> (fire.getBehaviour() instanceof FireballBehavior.PlayerControlled)
                            && fire.getOwner() == entity);

            if (fireball != null) {
                fireball.setDead();
                cleanUp();
            }
        }
    }

    @Override
    public int getTotalDuration() {
        AbilityData abilityData = bender.getData().getAbilityData(ability);
        int duration = 40;
        float maxSize = ability.getProperty(Ability.MAX_SIZE, abilityData).floatValue();
        float size = ability.getProperty(Ability.SIZE, abilityData).floatValue();
        int fireballs = (int) entity.world.getEntitiesWithinAABB(EntityFireball.class, entity.getEntityBoundingBox().grow(3.5, 3, 3.5))
                .stream().filter(entityFireball -> entityFireball.getOwner() == entity && entityFireball.getBehaviour()
                        instanceof FireballBehavior.PlayerControlled).count();
        if (maxSize > size)
            duration *= (maxSize / size);
        duration *= Math.max(1, fireballs);
        return duration;
    }

    @Override
    public int getWaitDuration() {
        return 15;
    }

    @Override
    public StatusControl[] getStatusControls() {
        return new StatusControl[]{
                THROW_FIREBALL
        };
    }

    @Override
    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        if (statusControl == THROW_FIREBALL) {
            return timeExecuting > 0 && timeExecuting % getWaitDuration() == 0
                    && bender.getData().getAbilityData(ability).getAbilityCooldown() <= 0;
        }
        return super.shouldExecStatCtrl(statusControl);
    }

    @Override
    public float getMaxTargetRange() {
        return 10;
    }

    @Override
    public float getMinTargetRange() {
        return 2.5F;
    }
}
