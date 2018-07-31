package com.crowsofwar.avatar.common.bending.air;

import net.minecraft.entity.*;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.gorecore.util.Vector;

import static com.crowsofwar.gorecore.util.Vector.*;
import static java.lang.Math.toDegrees;

public class AiCloudBall extends BendingAi {
	private int timeExecuting;

	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiCloudBall(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		timeExecuting = 0;
		setMutexBits(2);
	}

	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		data.chi().setMaxChi(10);
		data.chi().setTotalChi(10);
		data.chi().setAvailableChi(10);
		execAbility();
		data.getMiscData().setAbilityCooldown(100);
	}

	@Override
	public boolean shouldContinueExecuting() {

		if (entity.getAttackTarget() == null) return false;

		Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
		entity.rotationYaw = (float) toDegrees(rotations.y());
		entity.rotationPitch = (float) toDegrees(rotations.x());

		if (timeExecuting >= 40) {
			BendingData data = bender.getData();
			execStatusControl(StatusControl.THROW_CLOUDBURST);
			timeExecuting = 0;
			return false;
		} else {
			return true;
		}

	}

	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSq(target) > 4 * 4 && bender.getData().getMiscData().getAbilityCooldown() == 0 && entity.getRNG()
						.nextBoolean();
	}

	@Override
	public void updateTask() {
		timeExecuting++;
	}

	@Override
	public void resetTask() {

		EntityCloudBall cloudball = AvatarEntity.lookupEntity(entity.world, EntityCloudBall.class, //
															  cloud -> cloud.getBehavior() instanceof CloudburstBehavior.PlayerControlled
																			  && cloud.getOwner() == entity);

		if (cloudball != null) {
			cloudball.setDead();
			bender.getData().removeStatusControl(StatusControl.THROW_CLOUDBURST);
		}

	}

}
