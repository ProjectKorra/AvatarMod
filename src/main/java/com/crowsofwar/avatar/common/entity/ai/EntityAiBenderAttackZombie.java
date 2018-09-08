package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntityHumanBender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class EntityAiBenderAttackZombie  extends EntityAIBase {

	private final EntityHumanBender bender;
	private final double followRange;

	public EntityAiBenderAttackZombie(EntityHumanBender bender) {
		this.bender = bender;
		this.followRange = 20;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		boolean nearbyZombie = false;
		AxisAlignedBB box= new AxisAlignedBB(bender.posX + 20, bender.posY + 5, bender.posZ + 20, bender.posX - 20, bender.posY - 5,
				bender.posZ - 20);
		List<EntityZombie> zombie = bender.world.getEntitiesWithinAABB(EntityZombie.class, box);
		if (!zombie.isEmpty()) {
			nearbyZombie = true;
		}

		return nearbyZombie;
	}

	@Override
	public boolean shouldContinueExecuting() {

		AxisAlignedBB box = new AxisAlignedBB(bender.posX + 20, bender.posY + 5, bender.posZ + 20, bender.posX - 20, bender.posY - 5,
				bender.posZ - 20);
		List<EntityZombie> zombie = bender.world.getEntitiesWithinAABB(EntityZombie.class, box);
		if (!zombie.isEmpty()) {
			EntityZombie z =  zombie.get(0);
			if (z == null || z.isDead) {
				return false;
			}

			bender.setAttackTarget(z);

			if (bender.getDistanceSq(z) > followRange * followRange) {
				bender.setAttackTarget(null);
				return false;
			}
			

			bender.getMoveHelper().setMoveTo(z.posX, z.posY, z.posZ, 1);
			bender.getLookHelper().setLookPositionWithEntity(z, 20, 20);



		}
		return !zombie.isEmpty();
	}

}

