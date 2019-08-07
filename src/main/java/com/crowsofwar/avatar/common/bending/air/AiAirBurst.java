package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class AiAirBurst extends BendingAi {

	AiAirBurst(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		setMutexBits(4);
	}

	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		execAbility();
		data.getMiscData().setAbilityCooldown(60);
	}

	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistance(target) <= 7
				&& bender.getData().getMiscData().getAbilityCooldown() == 0/* && entity.getRNG().nextBoolean()**/ && !bender.getData().hasTickHandler(TickHandlerController.AIRBURST_CHARGE_HANDLER);
	}


}
