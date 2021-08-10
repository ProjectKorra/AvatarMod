package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.Bender;
import net.minecraft.entity.EntityLiving;

public class AiFireShot extends BendingAi {

	AiFireShot(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		setMutexBits(3);
	}

	@Override
	protected boolean shouldExec() {
		return entity.getAttackTarget() != null && entity.world.rand.nextBoolean();
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
		return 1;
	}

	@Override
	public int getWaitDuration() {
		return 2;
	}

	@Override
	public int getTotalDuration() {
		return 10;
	}

	@Override
	public boolean shouldExecAbility() {
		return timeExecuting >= getWaitDuration();
	}
}
