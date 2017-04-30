package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAiBisonHelpOwnerTarget extends EntityAITarget {
	
	private final EntitySkyBison bison;
	EntityLivingBase theTarget;
	private int timestamp;
	
	public EntityAiBisonHelpOwnerTarget(EntitySkyBison bison) {
		super(bison, false);
		this.bison = bison;
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
			
			// getLastAttacker() is a misnomer
			// really returns last entity that the owner attacked
			
			this.theTarget = owner.getLastAttacker();
			int i = owner.getLastAttackerTime();
			return i != this.timestamp && isSuitableTarget(this.theTarget, false);
		}
		
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theTarget);
		EntityLivingBase entitylivingbase = this.bison.getOwner();
		
		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getLastAttackerTime();
		}
		
		super.startExecuting();
	}
}