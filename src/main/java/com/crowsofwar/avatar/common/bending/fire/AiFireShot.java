package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class AiFireShot extends BendingAi {

	AiFireShot(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		setMutexBits(2);
	}

	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		execAbility();
		data.getMiscData().setAbilityCooldown(30);
	}


	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistance(target) < 10
				&& bender.getData().getMiscData().getAbilityCooldown() == 0 && entity.getRNG().nextBoolean();
	}


}
