package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

public class AiFireShot extends BendingAi {

	AiFireShot(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		setMutexBits(2);
	}

	@Override
	protected void startExec() {
		EntityLivingBase target = entity.getAttackTarget();
		BendingData data = bender.getData();

		if (target != null) {
			Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
			entity.rotationYaw = (float) toDegrees(rotations.y());
			entity.rotationPitch = (float) toDegrees(rotations.x());

			execAbility();
			data.getMiscData().setAbilityCooldown(20);

		}
	}


	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSq(target) < 10 * 10
				&& bender.getData().getMiscData().getAbilityCooldown() == 0 && entity.getRNG().nextBoolean();
	}

}
