package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class RestoreParticleHandler extends TickHandler {

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData aD = data.getAbilityData("restore");
		World world = ctx.getWorld();
		int duration = data.getTickHandlerDuration(this);
		int restoreDuration = aD.getLevel()  > 0 ? 60 + 10 * aD.getLevel() : 60;
		Random rand = new Random();
		double r = rand.nextDouble();
		if (!world.isRemote) {
			for (int i = 0; i < 90; i++) {
				WorldServer World = (WorldServer) world;
				Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 4) + (r * 2)), 0).withY(0);
				World.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, location.x() + entity.posX, location.y() + entity.getEntityBoundingBox().minY + r * 2,
						location.z() + entity.posZ, 1, 0, 0, 0, 10D);
				//World.spawnParticle(EnumParticleTypes.);
			}
		}
		return duration >= restoreDuration;
	}
}
