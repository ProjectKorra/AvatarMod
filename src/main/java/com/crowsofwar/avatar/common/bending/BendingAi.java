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

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import static com.crowsofwar.avatar.common.bending.BendingAi.AbilityType.PROJECTILE;

/**
 * Represents behavior needed for use of an ability by a mob. When most
 * abilities are activated, some sort of preparation or strategy is required.
 * For example, air gust is only useful when an enemy is too close, and requires
 * the user to aim at an enemy entity. This class wraps all of this behavior so
 * the ability can be activated at the appropriate time.
 * <p>
 * BendingAi is a subclass of EntityAIBase, meaning that a new instance is
 * applied per-entity in its tasks list. A new instance of a BendingAi is
 * acquired via the ability's {@link Ability#getAi(EntityLiving, Bender)
 * getAi method} for the specific mob.
 *
 * @author CrowsOfWar
 */
public abstract class BendingAi extends EntityAIBase {

    protected final Ability ability;
    protected final EntityLiving entity;
    protected final Bender bender;

    protected int timeExecuting;

    protected BendingAi(Ability ability, EntityLiving entity, Bender bender) {
        this.ability = ability;
        this.entity = entity;
        this.bender = bender;
        this.timeExecuting = 0;
    }

    @Override
    public void startExecuting() {
        timeExecuting = 0;
        startExec();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void resetTask() {
        timeExecuting = 0;
    }

    @Override
    public void updateTask() {
        timeExecuting++;
    }

    @Override
    public final boolean shouldExecute() {
        EntityLivingBase target = entity.getAttackTarget();
        boolean targetInRange = target == null ||
                entity.getDistanceSq(target) < getTargetRange() * getTargetRange();
        return targetInRange && shouldExec();
    }

    protected abstract boolean shouldExec();

    protected abstract void startExec();

    /**
     * Executes the ability's main code (the part used for players)
     */
    protected void execAbility() {
        bender.executeAbility(ability, false);
    }

    /**
     * If the status control is present, uses up the status control
     */
    protected void execStatusControl(StatusControl sc) {
        BendingData data = bender.getData();
        if (data.hasStatusControl(sc)) {
            Raytrace.Result raytrace = Raytrace.getTargetBlock(entity, ability.getRaytrace());
            if (sc.execute(new BendingContext(data, entity, bender, raytrace))) {
                data.removeStatusControl(sc);
            }
        }
    }

    public AbilityType[] getAbilityTypes() {
        return new AbilityType[] {
                PROJECTILE
        };
    }

    public enum AbilityType {
        PROJECTILE,
        OFFENSIVE,
        BUFF,
        UTILITY,
        MOBILITY,
        DEFENSIVE
    }

    public int getTargetRange() {
        return 12;
    }

}
