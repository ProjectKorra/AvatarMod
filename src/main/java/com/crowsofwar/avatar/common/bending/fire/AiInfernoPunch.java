package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAiMelee;
import com.crowsofwar.avatar.common.data.Bender;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;

public class AiInfernoPunch extends BendingAiMelee {


	public AiInfernoPunch(Ability ability, EntityLiving entity, Bender bender, EntityCreature creature, double speedIn, boolean useLongMemory) {
		super(ability, entity, bender, speedIn, useLongMemory);
	}

	@Override
	protected void startExec() {
		super.startExec();
		bender.getData().getMiscData().setAbilityCooldown(80);
	}


}
