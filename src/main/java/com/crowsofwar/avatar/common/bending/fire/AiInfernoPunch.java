package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.BendingAiMelee;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

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
