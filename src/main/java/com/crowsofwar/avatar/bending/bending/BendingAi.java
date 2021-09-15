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
package com.crowsofwar.avatar.bending.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.bending.bending.BendingAi.AbilityType.*;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

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
        AvatarLog.info("Client Side: " + entity.world.isRemote);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return applyCustomBehaviour();
    }

    @Override
    public void resetTask() {
        timeExecuting = 0;
        cleanUp();
    }

    public boolean applyCustomBehaviour() {
        if (Arrays.stream(getAbilityTypes()).anyMatch(abilityType -> abilityType == PROJECTILE
                || abilityType == OFFENSIVE)) {
            if (entity.getAttackTarget() == null)
                return false;
            lookAtAttackTarget();
            if (shouldExecAbility())
                execAbility();
            for (StatusControl sc : getStatusControls()) {
                if (shouldExecStatCtrl(sc))
                    execStatusControl(sc);
            }

        } else if (Arrays.stream(getAbilityTypes()).anyMatch(abilityType -> abilityType == MOBILITY)) {
            Random rand = entity.world.rand;
            if (shouldExecAbility())
                execAbility();
            if (timeExecuting >= getWaitDuration()) {
                //Move in the right direction
                lookAtTarget(entity.getPositionVector().add(rand.nextGaussian() / 2, entity.getEyeHeight() + rand.nextGaussian() / 2,
                        rand.nextGaussian() / 2));
                for (StatusControl sc : getStatusControls()) {
                    if (shouldExecStatCtrl(sc))
                        execStatusControl(sc);
                }
                return false;

            }
        } else {
            if (shouldExecAbility())
                execAbility();
            for (StatusControl sc : getStatusControls()) {
                if (shouldExecStatCtrl(sc))
                    execStatusControl(sc);
            }
            return false;
        }
        if (timeExecuting >= getTotalDuration()) {
            cleanUp();
        }
        return timeExecuting < getTotalDuration();

    }

    @Override
    public void updateTask() {
        super.updateTask();
        timeExecuting++;
    }

    @Override
    public final boolean shouldExecute() {
        EntityLivingBase target = entity.getAttackTarget();
        boolean targetInRange = target == null ||
                entity.getDistanceSq(target) < getMaxTargetRange() * getMaxTargetRange() &&
                        entity.getDistanceSq(target) > getMinTargetRange() * getMinTargetRange();
        return targetInRange && shouldExec();
    }


    protected abstract boolean shouldExec();

    protected abstract void startExec();

    /**
     * Executes the ability's main code (the part used for players)
     */
    protected void execAbility() {
        if (Objects.requireNonNull(AbilityData.get(entity, ability.getName())).getAbilityCooldown() <= 0)
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
        return new AbilityType[]{
                PROJECTILE
        };
    }

    public float getMaxTargetRange() {
        return 8;
    }

    public float getMinTargetRange() {
        return 2;
    }

    public Ability getAbility() {
        return ability;
    }

    //TODO: Redo status controls to be an array,
    //and have a function per status control that defines if it should execute
    public void cleanUp() {
        for (StatusControl sc : getStatusControls())
            if (bender.getData().hasStatusControl(sc))
                bender.getData().removeStatusControl(sc);

    }

    public boolean shouldExecAbility() {
        return true;
    }

    public StatusControl[] getStatusControls() {
        return new StatusControl[0];
    }

    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        return timeExecuting >= getWaitDuration();
    }

    public void lookAtAttackTarget() {
        if (entity.getAttackTarget() != null) {
            EntityLivingBase target = entity.getAttackTarget();

            Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
            entity.rotationYaw = (float) toDegrees(rotations.y());
            entity.rotationPitch = (float) toDegrees(rotations.x());

            entity.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ,
                    entity.getHorizontalFaceSpeed() * 5, entity.getVerticalFaceSpeed() * 5);
        }
    }

    public void lookAtTarget(Vec3d target) {
        if (entity.getAttackTarget() != null) {

            Vector rotations = getRotationTo(getEntityPos(entity), new Vector(target));
            entity.rotationYaw = (float) toDegrees(rotations.y());
            entity.rotationPitch = (float) toDegrees(rotations.x());

            entity.getLookHelper().setLookPosition(target.x, target.y, target.z,
                    entity.getHorizontalFaceSpeed() * 5, entity.getVerticalFaceSpeed() * 5);
        }
    }

    public int getWaitDuration() {
        return 10;
    }

    public boolean isConstant() {
        return false;
    }

    public int getTotalDuration() {
        return 40;
    }

    public enum AbilityType {
        PROJECTILE,
        OFFENSIVE,
        BUFF,
        UTILITY,
        MOBILITY,
        DEFENSIVE
    }

}
