package com.crowsofwar.avatar.common.bending.fire;

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
import net.minecraft.world.WorldServer;

import java.util.Random;


public class PurifyParticleHandler extends TickHandler {

	private final ParticleSpawner particles;

	private PurifyParticleHandler() {
		particles = new NetworkParticleSpawner();
	}

	static TickHandler PURIFY_PARTICLE_SPAWNER = new PurifyParticleHandler();

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData aD = data.getAbilityData("purify");
		World world = ctx.getWorld();
		int duration = data.getTickHandlerDuration(this);
		int immolateDuration = aD.getLevel()  > 0 ? 60 + 40 * aD.getLevel() : 40;
		//The particles take a while to disappear after the ability finishes- so you decrease the time the particles can spawn
		Random rand = new Random();
		double r = rand.nextDouble();
		if (!world.isRemote) {
			for (int i = 0; i < 18; i++) {
				WorldServer World = (WorldServer) world;
				int random = rand.nextInt(2) + 1;
				r = random == 1 ? r : r * -1;
				Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 20) + (r * 2)), 0).times(0.5).withY(entity.getEyeHeight()-0.7);
				particles.spawnParticles(world, EnumParticleTypes.FLAME, 1, 1, location.plus(Vector.getEntityPos(entity)),
							new Vector(0.6, 1.8, 0.6));
				}
			}
		return duration >= immolateDuration;
	}
}

