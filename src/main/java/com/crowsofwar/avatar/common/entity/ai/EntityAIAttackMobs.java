package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;

import java.util.List;

public class EntityAIAttackMobs extends EntityAITarget {
	EntityBender bender;
	EntityMob mob;

	public EntityAIAttackMobs(EntityBender bender) {
		super(bender, true, true);
		this.bender = bender;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		List<EntityMob> nearbyMobs = bender.world.getEntitiesWithinAABB(EntityMob.class, bender.getEntityBoundingBox().grow(20, 0, 20));
		if (!nearbyMobs.isEmpty()) {
			this.mob = nearbyMobs.get(0);
			return true;
		}
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.bender.setAttackTarget(this.mob);
		super.startExecuting();
	}
}
