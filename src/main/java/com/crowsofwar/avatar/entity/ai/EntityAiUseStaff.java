package com.crowsofwar.avatar.entity.ai;

import com.crowsofwar.avatar.entity.mob.EntityHumanBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAiUseStaff extends EntityAIBase {

	private final EntityHumanBender bender;
	private int timeExecuting;


	public EntityAiUseStaff(EntityHumanBender bender) {
		this.bender = bender;
		setMutexBits(1);
		this.timeExecuting = 0;
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


