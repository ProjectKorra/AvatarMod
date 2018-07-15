package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WaterParticleSpawner extends TickHandler {

	private int maxDuration;
	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = AbilityData.get(entity, "water_cannon");
		int maxDuration = 40;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			maxDuration = 60;
		}

		int duration = data.getTickHandlerDuration(this);

		if (data.hasTickHandler(WATER_CHARGE) && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			 if (duration % 2 == 0) {
				 for (int degree = 0; degree < 360; degree++) {
					 double radians = Math.toRadians(degree);
					 double x = Math.sin(radians) * ((maxDuration - duration) / 10) > 0 ? Math.sin(radians) : Math.sin(radians) * ((maxDuration - duration) / 10);
					 double z = Math.sin(radians) * ((maxDuration - duration) / 10) > 0 ? Math.sin(radians) : Math.sin(radians) * ((maxDuration - duration) / 10);
					 //Prevents a possible null pointer exception with the value being zero
					 double y = entity.posY;
					 World.spawnParticle(EnumParticleTypes.WATER_SPLASH, x + entity.posX, y, z + entity.posZ, 50, 0, 0, 0, (double) 0);
					 //NOTE: The higher the amount of particles, the smaller the part of the circle. Lower is the full circle.
				 }
			 }

			return false;
		}
		else return true;
	}

	public void setMaxDuration(int duration) {
		this.maxDuration = duration;
	}
	//So I can determine how long the particles should come inward
}
