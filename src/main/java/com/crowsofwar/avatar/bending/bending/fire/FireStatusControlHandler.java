package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFireball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;

public class FireStatusControlHandler extends TickHandler {

	private int ticks = 0;

	public FireStatusControlHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);

		EntityFireball ball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, entity);
		if (ball == null && data.hasStatusControl(THROW_FIREBALL)) {
			ticks++;
			if (ticks >= 20) {
				data.removeStatusControl(THROW_FIREBALL);
				ticks = 0;
				return true;
			}
		}

		return duration >= 40;

	}
}
