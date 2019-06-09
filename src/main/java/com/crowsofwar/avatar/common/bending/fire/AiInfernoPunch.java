package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;

import net.minecraft.entity.EntityLiving;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;

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
		if(data.getAbilityData(this.ability).isMasterPath(AbilityTreePath.FIRST)) data.addStatusControl(INFERNO_PUNCH_FIRST);
		else if(data.getAbilityData(this.ability).isMasterPath(AbilityTreePath.SECOND)) data.addStatusControl(INFERNO_PUNCH_SECOND);
		else data.addStatusControl(INFERNO_PUNCH_MAIN);
		data.getMiscData().setAbilityCooldown(60);

	}

	@Override
	public void updateTask() {
		timeExecuting++;
	}



}
