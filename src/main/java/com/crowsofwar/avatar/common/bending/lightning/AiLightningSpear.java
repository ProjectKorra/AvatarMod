package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

public class AiLightningSpear extends BendingAi {
    /**
     * @param ability
     * @param entity
     * @param bender
     */
    protected AiLightningSpear(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
    }

    @Override
    protected void startExec() {
        EntityLivingBase target = entity.getAttackTarget();
        Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
        entity.rotationYaw = (float) toDegrees(rotations.y());
        entity.rotationPitch = (float) toDegrees(rotations.x());
    }

    @Override
    public boolean shouldContinueExecuting() {
        entity.rotationYaw = entity.rotationYawHead;
        if (timeExecuting >= 40 && entity.getAttackTarget() != null) {
            Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
            entity.rotationYaw = (float) toDegrees(rotations.y());
            entity.rotationPitch = (float) toDegrees(rotations.x());
            execAbility();
            return false;
        }
        return true;
    }

    @Override
    protected boolean shouldExec() {

        EntityLivingBase target = entity.getAttackTarget();

        if (target != null) {
            double dist = entity.getDistanceSqToEntity(target);
            return dist >= 4 * 4;
        }

        return false;

    }

}


