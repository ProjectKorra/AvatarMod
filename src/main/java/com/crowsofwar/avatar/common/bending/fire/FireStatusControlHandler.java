package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class FireStatusControlHandler extends TickHandler {

	public static TickHandler FIRE_STATCTRL_HANDLER = new FireStatusControlHandler();

	private int ticks = 0;
	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);


		EntityFireball ball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, entity);
		if (ball == null && data.hasStatusControl(StatusControl.THROW_FIREBALL)) {
			ticks++;
			if (ticks >= 20) {
				data.removeStatusControl(StatusControl.THROW_FIREBALL);
				ticks = 0;
				return true;
			}
		}
		EntityFireArc arc = AvatarEntity.lookupControlledEntity(world, EntityFireArc.class, entity);
		if (arc == null && data.hasStatusControl(StatusControl.THROW_FIRE)) {
			ticks++;
			if (ticks >= 20) {
				data.removeStatusControl(StatusControl.THROW_FIRE);
				ticks = 0;
				return true;
			}
		}

		return duration >= 40;

	}
}
