package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RestoreParticleHandler extends TickHandler {
	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData aD = data.getAbilityData("restore");
		World world = ctx.getWorld();
		int duration = data.getTickHandlerDuration(this);
		int restoreDuration = 60 + 50 * aD.getLevel();
		if (!world.isRemote) {
			WorldServer World = (WorldServer) world;
			double maxHeight = 3;
			double heightUnit = maxHeight / 2.0 / Math.PI;
			double step = Math.PI / 16;
			double radius = 1.5;
			for (double rad = 0; rad < Math.PI * 2; rad += step) {
				double x = Math.cos(rad) * radius;
				double z = Math.sin(rad) * radius;
				double y = heightUnit * rad;
				World.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, x + entity.posX, y + entity.getEntityBoundingBox().minY, z + entity.posZ,
						10, 0, 0, 0, 0.3);
			}
		}
		return duration >= restoreDuration;
	}
}
