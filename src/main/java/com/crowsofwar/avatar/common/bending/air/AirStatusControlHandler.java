package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class AirStatusControlHandler extends TickHandler {

	private int ticks = 0;
	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);


		EntityCloudBall ball = AvatarEntity.lookupControlledEntity(world, EntityCloudBall.class, entity);
		if (ball == null && data.hasStatusControl(StatusControl.THROW_CLOUDBURST)) {
			ticks++;
			if (ticks >= 20) {
				data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
				ticks = 0;
				return true;
			}
		}
		return duration >= 40;

	}
}
