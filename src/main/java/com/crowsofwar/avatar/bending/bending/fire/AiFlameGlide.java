package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import net.minecraft.entity.EntityLiving;

public class AiFlameGlide extends BendingAi {

    AiFlameGlide(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
        setMutexBits(3);
    }

    @Override
    protected boolean shouldExec() {
        return entity.getAttackTarget() != null &&
                entity.world.rand.nextDouble() / 2 > (1F - timeExecuting) / (float) getTotalDuration();
    }

    @Override
    protected void startExec() {

    }

    @Override
    public float getMaxTargetRange() {
        return 5;
    }

    @Override
    public float getMinTargetRange() {
        return 0;
    }

    @Override
    public AbilityType[] getAbilityTypes() {
        AbilityType[] types = new AbilityType[1];
        types[0] = AbilityType.MOBILITY;
        return types;
    }

    @Override
    public int getWaitDuration() {
        return 2;
    }

    @Override
    public int getTotalDuration() {
        return 20;
    }

    @Override
    public boolean shouldExecAbility() {
        return timeExecuting >= getWaitDuration();
    }

    @Override
    public StatusControl[] getStatusControls() {
        StatusControl[] ctrls = new StatusControl[1];
        ctrls[0] = StatusControlController.FIRE_JUMP;
        return ctrls;
    }
}
