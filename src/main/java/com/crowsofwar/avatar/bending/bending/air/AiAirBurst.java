package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import net.minecraft.entity.EntityLiving;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

public class AiAirBurst extends BendingAi {

    AiAirBurst(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
        setMutexBits(3);
    }

    @Override
    protected boolean shouldExec() {
        return entity.getAttackTarget() != null;
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
        return getAbility().getProperty(Ability.CHARGE_TIME, bender.getData().getAbilityData("air_burst")).intValue() - AvatarUtils.getRandomNumberInRange(0,
                (int) (getAbility().getProperty(Ability.CHARGE_TIME, bender.getData().getAbilityData("air_burst")).intValue() * 0.75)) + getWaitDuration();
    }

    @Override
    public boolean shouldExecAbility() {
        return timeExecuting >= getWaitDuration();
    }

    @Override
    public StatusControl[] getStatusControls() {
        return new StatusControl[]{
                StatusControlController.SHOOT_AIR_BURST,
                StatusControlController.CHARGE_AIR_BURST,
                StatusControlController.RELEASE_AIR_BURST
        };
    }

    @Override
    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        if (statusControl == CHARGE_AIR_BURST)
            return timeExecuting >= getWaitDuration();
        if (statusControl == RELEASE_AIR_BURST || statusControl == SHOOT_AIR_BURST)
            if (statusControl == SHOOT_AIR_BURST)
                return entity.getAttackTarget() != null && entity.getAttackTarget().getDistance(entity) > 4 && timeExecuting >= getTotalDuration();
            else return timeExecuting >= getTotalDuration();
        return super.shouldExecStatCtrl(statusControl);
    }

    @Override
    public AbilityType[] getAbilityTypes() {
        return new AbilityType[]{
                AbilityType.OFFENSIVE,
                AbilityType.PROJECTILE
        };
    }
}
