package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Objects;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class FireDevourTickHandler extends TickHandler {
	private ParticleSpawner particles;

	public FireDevourTickHandler() {
		this.particles = new NetworkParticleSpawner();
	}

	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		Vector eye = Vector.getEyePos(entity);
		double range = STATS_CONFIG.fireSearchRadius;
		for (int i = 0; i < STATS_CONFIG.fireAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.fireAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.fireAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.fireAngles;

				BiPredicate<BlockPos, IBlockState> isFire = (pos, state) -> state.getBlock() == Blocks.FIRE;

				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isFire);

				if (result.hitSomething() && result.getPosPrecise() != null) {
					double position = Vector.getEntityPos(entity).dist(result.getPosPrecise());
					Vector pos = Vector.getEntityPos(entity).plusY(entity.getEyeHeight());
					for (double h = position; h > 0;) {
						if (world instanceof WorldServer && !world.isRemote) {
							WorldServer World = (WorldServer) world;
							World.spawnParticle(EnumParticleTypes.FLAME, pos.x() + position, pos.y(), pos.z() + position, 1, 0, 0, 0, 0D);
						}
						else {
							particles.spawnParticles(world, EnumParticleTypes.FLAME, 1, 2, pos.x() + position, pos.y(), pos.z() + position, 0, 0, 0);
						}
						h -= 0.1;
					}
					world.setBlockToAir(Objects.requireNonNull(result.getPosPrecise()).toBlockPos());
				}

			}
		}
		return ctx.getData().getTickHandlerDuration(this) >= 10;
	}
}
