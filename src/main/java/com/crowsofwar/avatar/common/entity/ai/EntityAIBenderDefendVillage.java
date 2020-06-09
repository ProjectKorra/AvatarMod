package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.village.Village;

public class EntityAIBenderDefendVillage extends EntityAITarget {
	EntityBender bender;
	/**
	 * The aggressor of the iron golem's village which is now the golem's attack target.
	 */
	EntityLivingBase villageAgressorTarget;

	public EntityAIBenderDefendVillage(EntityBender bender) {
		super(bender, false, true);
		this.bender = bender;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		Village village = bender.world.getVillageCollection().getNearestVillage(bender.getPosition(), 50);

		if (village == null) {
			return false;
		} else {
			this.villageAgressorTarget = village.findNearestVillageAggressor(this.bender);

			if (this.villageAgressorTarget instanceof EntityCreeper) {
				return false;
			} else if (this.isSuitableTarget(this.villageAgressorTarget, false)) {
				return true;
			} else if (this.taskOwner.getRNG().nextInt(20) == 0) {
				this.villageAgressorTarget = village.getNearestTargetPlayer(this.bender);
				return this.isSuitableTarget(this.villageAgressorTarget, false);
			} else {
				return false;
			}
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.bender.setAttackTarget(this.villageAgressorTarget);
		super.startExecuting();
	}
}