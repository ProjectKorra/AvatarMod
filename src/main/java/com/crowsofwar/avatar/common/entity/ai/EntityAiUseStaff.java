package com.crowsofwar.avatar.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import com.crowsofwar.avatar.common.entity.mob.EntityHumanBender;

public class EntityAiUseStaff extends EntityAIBase {

	private final EntityHumanBender bender;
	private int timeExecuting;

	public EntityAiUseStaff(EntityHumanBender bender) {
		this.bender = bender;
		setMutexBits(1);
		timeExecuting = 0;
	}

	@Override
	public boolean shouldExecute() {
		return bender.getAttackTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting() {

		if (bender.getAttackTarget() != null) {
			EntityLivingBase attacker = bender.getAttackTarget();
			bender.getMoveHelper().setMoveTo(attacker.posX, attacker.posY, attacker.posZ, 1);
			bender.getLookHelper().setLookPositionWithEntity(attacker, 20, 20);
			bender.getHeldItemMainhand().getItem().onEntitySwing(bender, bender.getHeldItemMainhand());
			return false;
		}
		return timeExecuting <= 20;
	}

	@Override
	public void updateTask() {
		timeExecuting++;
	}
}


