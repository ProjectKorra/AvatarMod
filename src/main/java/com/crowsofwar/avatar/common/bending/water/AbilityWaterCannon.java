package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class AbilityWaterCannon extends Ability {
	public AbilityWaterCannon() {
		super(Waterbending.ID, "water_cannon");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		Vector targetPos = getClosestWaterbendableBlock(entity, ctx.getLevel());
		boolean hasChi = bender.consumeChi(STATS_CONFIG.chiWaterCannon);
		boolean hasWaterCharge = data.hasTickHandler(TickHandler.WATER_CHARGE);

		if (ctx.consumeWater(3) || ctx.consumePlants(3) || ctx.consumeSnow(3)) {
			if (hasChi && !hasWaterCharge) {
				ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
			}
		} else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (!hasWaterCharge) {
				ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
			}
		} else if (targetPos != null) {
			if (hasChi && !hasWaterCharge) {
				world.setBlockToAir(targetPos.toBlockPos());
				Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

				
				/*EntityEarthspike spike = new EntityEarthspike(world);
				spike.setPosition(targetPos);
				spike.setOwner(entity);
				world.spawnEntity(spike);**/
				//For debugging
				
				ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
			}
		} else {
			bender.sendMessage("avatar.waterCannonFail");
			}
		}


	private Vector getClosestWaterbendableBlock (EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterCannonSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterCannonAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterCannonAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterCannonAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterCannonAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> STATS_CONFIG.waterBendableBlocks.contains(state.getBlock())
						|| STATS_CONFIG.plantBendableBlocks.contains(state.getBlock());


				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

	}

}

