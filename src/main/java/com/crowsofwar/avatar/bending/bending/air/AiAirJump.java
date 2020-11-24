package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import net.minecraft.entity.EntityLiving;

import static com.crowsofwar.avatar.util.data.StatusControlController.AIR_JUMP;

public class AiAirJump extends BendingAi {

    AiAirJump(Ability ability, EntityLiving entity, Bender bender) {
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
       return new AbilityType[] {
                AbilityType.MOBILITY
        };
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
        return new StatusControl[] {
                AIR_JUMP
        };
    }
}
