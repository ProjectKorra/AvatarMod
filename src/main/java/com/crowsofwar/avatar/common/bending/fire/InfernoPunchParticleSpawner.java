package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class InfernoPunchParticleSpawner extends TickHandler {
	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		if (data.hasStatusControl(StatusControl.INFERNO_PUNCH) && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			if (data.getTickHandlerDuration(this) % 5 == 0) {
				double y = entity.posY + entity.getEyeHeight() - 0.65;
				World.spawnParticle(EnumParticleTypes.FLAME, entity.posX, y, entity.posZ, 20, 0, 0, 0, 0.015);

			}

			return false;
		} else return true;
	}
}
