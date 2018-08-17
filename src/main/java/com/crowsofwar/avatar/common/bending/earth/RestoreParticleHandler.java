package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class RestoreParticleHandler extends TickHandler {

	private final ParticleSpawner particles;

	public RestoreParticleHandler() {
		particles = new NetworkParticleSpawner();
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData aD = data.getAbilityData("restore");
		World world = ctx.getWorld();
		int duration = data.getTickHandlerDuration(this);
		int restoreDuration = aD.getLevel()  > 0 ? 60 + 10 * aD.getLevel() : 60;
		if (!world.isRemote) {
			for (int i = 0; i < 8; i++) {
				Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 45)), 0).times(1.5).withY(0);
				particles.spawnParticles(world, EnumParticleTypes.VILLAGER_HAPPY, 2, 7,
						location.plus(Vector.getEntityPos(entity)), new Vector(.01, 1, .01));
			}
		}
		return duration >= restoreDuration;
	}
}
