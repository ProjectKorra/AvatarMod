package com.crowsofwar.avatar.bending.bending.earth.tickhandlers;

import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

public class RestoreParticleHandler extends TickHandler {
	private final ParticleSpawner particles;

	public RestoreParticleHandler(int id) {
		super(id);
		particles = new NetworkParticleSpawner();
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		AbilityData aD = data.getAbilityData("restore");
		World world = ctx.getWorld();
		int duration = data.getTickHandlerDuration(this);
		int restoreDuration = aD.getLevel() > 0 ? 40 + 30 * aD.getLevel() : 40;
		//The particles take a while to disappear after the ability finishes- so you decrease the time the particles can spawn
		Random rand = new Random();
		double r = rand.nextDouble();
		if (!world.isRemote) {
			for (int i = 0; i < 40; i++) {
				WorldServer World = (WorldServer) world;
				int random = rand.nextInt(2) + 1;
				r = random == 1 ? r : r * -1;
				Vector location = Vector.toRectangular(Math.toRadians(entity.rotationYaw + (i * 9) + (r * 2)), 0).times(0.5).withY(0);
				if (!CLIENT_CONFIG.useCustomParticles) {
					World.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, location.x() + entity.posX, location.y() + entity.getEntityBoundingBox().minY + (r * 2),
							location.z() + entity.posZ, 1, 0, 0, 0, 10D);
				} else {
					particles.spawnParticles(world, AvatarParticles.getParticleRestore(), 1, 2, location.plus(Vector.getEntityPos(entity)),
							new Vector(0.2, 0.65, 0.2), true);
				}
			}
		}
		return duration >= restoreDuration;
	}
}
