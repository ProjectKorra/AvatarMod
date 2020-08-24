package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityCloudBall;
import com.crowsofwar.avatar.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_CLOUDBURST;

public class AiCloudBall extends BendingAi {


    /**
     * @param ability The Ability
     * @param entity  The entity
     * @param bender  The bender, from the entity
     */
    protected AiCloudBall(Ability ability, EntityLiving entity, Bender bender) {
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
            EntityCloudBall cloudball = AvatarEntity.lookupEntity(entity.world, EntityCloudBall.class, //
                    cloudBall -> (cloudBall.getBehaviour() instanceof CloudburstBehavior.PlayerControlled
                            || cloudBall.getBehaviour() instanceof AbilityFireball.FireballOrbitController)
                            && cloudBall.getOwner() == entity);

            if (cloudball != null) {
                cloudball.setDead();
                cleanUp();
            }
        }
    }

    @Override
    public int getTotalDuration() {
        return 40;
    }

    @Override
    public int getWaitDuration() {
        return 15;
    }

    @Override
    public StatusControl[] getStatusControls() {
        return new StatusControl[]{
                THROW_CLOUDBURST
        };
    }

    @Override
    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        if (statusControl == THROW_CLOUDBURST) {
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
