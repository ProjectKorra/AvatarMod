package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAiBisonDefendOwner extends EntityAITarget {
	
	private final EntitySkyBison bison;
	private EntityLivingBase ownersTarget;
	private int timestamp;
	
	public EntityAiBisonDefendOwner(EntitySkyBison bison) {
		super(bison, false);
		this.bison = bison;
		this.setMutexBits(1);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		
		EntityLivingBase owner = this.bison.getOwner();
		
		if (owner == null) {
			return false;
		} else {
			// getAITarget() returns last entity that attacked the player
			this.ownersTarget = owner.getAITarget();
			int i = owner.getRevengeTimer();
			return i != this.timestamp && this.isSuitableTarget(this.ownersTarget, false);
		}
		
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.ownersTarget);
		EntityLivingBase entitylivingbase = this.bison.getOwner();
		
		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getRevengeTimer();
		}
		
		super.startExecuting();
	}
}