package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.powerrating.FirebendingSunModifier;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

	//public static TickHandler FIRE_DEVOUR_HANDLER = new FireDevourTickHandler();

	private int fireConsumed = 0;
	private int handlerLength = 20;

	@Override
	public boolean tick(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		if (data.getTickHandlerDuration(this) < 2) {
			fireConsumed = 0;
		}

		double inverseRadius = (20F - ctx.getData().getTickHandlerDuration(this)) / 10;
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
					if (world instanceof WorldServer) {
						WorldServer World = (WorldServer) world;
						for (double k = 1.2; k > 0; k -= 0.2) {
							for (int h = 0; h < 12; h++) {
								Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
										h * 30), 0).times(k).withY(entity.getEyeHeight() / 2);
								World.spawnParticle(EnumParticleTypes.FLAME, lookpos.x() + entity.posX, lookpos.y() + entity.getEntityBoundingBox().minY,
										lookpos.z() + entity.posZ, 2, 0, 0, 0, 0.05);
							}
						}

					}
					world.setBlockToAir(result.getPosPrecise().toBlockPos());
					fireConsumed++;
					handlerLength += 10;
					FireDevourPowerModifier modifier = new FireDevourPowerModifier();
					/*if (data.getPowerRatingManager(Firebending.ID).hasModifier(FireDevourPowerModifier.class)) {
						if (data.getPowerRatingManager(Firebending.ID).getModifiers() != null) {
							if (data.getPowerRatingManager(Firebending.ID).getModifiers().contains(new FireDevourPowerModifier())) {
								data.getPowerRatingManager(Firebending.ID).removeModifier(new FireDevourPowerModifier(), ctx);
							}
						}
					}**/

					Objects.requireNonNull(data.getPowerRatingManager(Firebending.ID)).clearModifiers(ctx);
					double power = Objects.requireNonNull(data.getPowerRatingManager(Firebending.ID)).getRating(ctx);
					modifier.setTicks(handlerLength);
					modifier.setPowerRating((fireConsumed * 5) + power);
					Objects.requireNonNull(data.getPowerRatingManager(Firebending.ID)).addModifier(modifier, ctx);

				}

			}
		}
		if (entity instanceof EntityPlayer) {
			return !entity.isSneaking();
		} else return data.getTickHandlerDuration(this) >= handlerLength;
	}
}
