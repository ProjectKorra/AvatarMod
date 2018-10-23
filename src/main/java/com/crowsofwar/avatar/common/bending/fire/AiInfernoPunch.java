package com.crowsofwar.avatar.common.bending.fire;

import net.minecraft.entity.EntityLiving;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;

import static com.crowsofwar.avatar.common.bending.fire.StatCtrlInfernoPunch.INFERNO_PUNCH;

public class AiInfernoPunch extends BendingAi {

	protected AiInfernoPunch(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}

	@Override
	protected boolean shouldExec() {
		return timeExecuting < 5;
	}

	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		execAbility();
		data.addStatusControl(INFERNO_PUNCH);
		data.getMiscData().setAbilityCooldown(60);

	}

	@Override
	public void updateTask() {
		timeExecuting++;
	}

}
