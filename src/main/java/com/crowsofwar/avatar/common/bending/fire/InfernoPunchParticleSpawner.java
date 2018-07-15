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
			for (int degree = 0; degree < 360; degree++) {
				double radians = Math.toRadians(degree);
				double x = Math.cos(degree);
				double z = Math.sin(degree);
				double y = entity.posY;
				World.spawnParticle(EnumParticleTypes.FLAME, x + entity.posX, y, z + entity.posZ, 1, 0, 0, 0, 0.01);
			}

			return false;
		}
		else return true;
	}
}
