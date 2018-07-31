package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import net.minecraft.entity.EntityLiving;

public class AiInfernoPunch extends BendingAi {

	protected AiInfernoPunch(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}

	@Override
	protected boolean shouldExec() {
		return false;
	}

	@Override
	protected void startExec() {

	}
}
