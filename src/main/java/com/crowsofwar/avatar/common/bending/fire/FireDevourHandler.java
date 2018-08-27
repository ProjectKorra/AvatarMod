package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireDevourHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onShift(LivingEvent.LivingUpdateEvent event) {
		Entity e = event.getEntity();
		World world = e.getEntityWorld();
		if (e == event.getEntity()) {
			if (e instanceof EntityPlayer || e instanceof EntityBender) {
				BendingData data = BendingData.get((EntityLivingBase) e);
				Bender b = Bender.get((EntityLivingBase) e);
				if (b != null) {
					if (e.isSneaking()) {
						ParticleSpawner particles = new ClientParticleSpawner();
						if (data.hasBendingId(Firebending.ID)) {
							Vector eye = Vector.getEyePos(e);
							double range = STATS_CONFIG.fireSearchRadius;
							for (int i = 0; i < STATS_CONFIG.fireAngles; i++) {
								for (int j = 0; j < STATS_CONFIG.fireAngles; j++) {

									double yaw = e.rotationYaw + i * 360.0 / STATS_CONFIG.fireAngles;
									double pitch = e.rotationPitch + j * 360.0 / STATS_CONFIG.fireAngles;

									BiPredicate<BlockPos, IBlockState> isFire = (pos, state) -> state.getBlock() == Blocks.FIRE;

									Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
									Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isFire);

									if (result.hitSomething()) {
										particles.spawnParticles(world, AvatarParticles.getParticleFlames(), 2, 10, result.getPosPrecise(),
												Vector.getEntityPos(e).minus(result.getPosPrecise()));
										world.setBlockToAir(Objects.requireNonNull(result.getPosPrecise()).toBlockPos());
									}

								}
							}
						}

					}
				}
			}
		}
	}
}
