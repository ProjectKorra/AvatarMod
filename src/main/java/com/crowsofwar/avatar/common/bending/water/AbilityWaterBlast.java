package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.WATER_CHARGE;
import static com.crowsofwar.avatar.common.data.TickHandlerController.WATER_PARTICLE_SPAWNER;
import static java.lang.Math.toRadians;

public class AbilityWaterBlast extends Ability {
	public AbilityWaterBlast() {
		super(Waterbending.ID, "water_blast");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		Vector targetPos = getClosestWaterbendableBlock(entity, ctx.getLevel() * 2);
		float chi = STATS_CONFIG.chiWaterCannon;
		//5
		boolean hasWaterCharge = data.hasTickHandler(WATER_CHARGE);
		int waterAmount = 2;

		if (ctx.getLevel() >= 2) {
			waterAmount = 3;
		}

		if (ctx.getLevel() == 2) {
			chi = STATS_CONFIG.chiWaterCannon * 1.2F;
			//6
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiWaterCannon * 1.6F;
			//8
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi = STATS_CONFIG.chiWaterCannon * 1.4F;
			//7
		}

		if (ctx.consumeWater(waterAmount)) {
			if (bender.consumeChi(chi) && !hasWaterCharge) {
				ctx.getData().addTickHandler(WATER_CHARGE);
				data.addTickHandler(WATER_PARTICLE_SPAWNER);
			}
		} else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (!hasWaterCharge) {
				ctx.getData().addTickHandler(WATER_CHARGE);
				data.addTickHandler(WATER_PARTICLE_SPAWNER);
			}
		} else if (targetPos != null && ctx.getLevel() >= 2) {
			if (bender.consumeChi(chi) && !hasWaterCharge) {
				world.setBlockToAir(targetPos.toBlockPos());
				//Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

				ctx.getData().addTickHandler(WATER_CHARGE);
				data.addTickHandler(WATER_PARTICLE_SPAWNER);
			}
		} else {
			bender.sendMessage("avatar.waterBlastFail");
		}
	}

	//Is broken; will investigate later.
	//TODO: Investigate if still broken. I don't think it is, I think I forgot to delete my comment
	private Vector getClosestWaterbendableBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterCannonSettings.waterCannonSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterCannonSettings.waterCannonAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterCannonSettings.waterCannonAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterCannonSettings.waterCannonAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterCannonSettings.waterCannonAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) ->
						(STATS_CONFIG.waterBendableBlocks.contains(state.getBlock()) || STATS_CONFIG.plantBendableBlocks
								.contains(state.getBlock())) && state.getBlock() != Blocks.AIR;

				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

	}

	/*private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterCannonSettings.waterCannonSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterCannonSettings.waterCannonAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterCannonSettings.waterCannonAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterCannonSettings.waterCannonAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterCannonSettings.waterCannonAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> state.getBlock() == Blocks.WATER
						|| state.getBlock() == Blocks.FLOWING_WATER || state.getBlock() == Blocks.ICE || state.getBlock() == Blocks.SNOW_LAYER
						|| state.getBlock() == Blocks.SNOW;

				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

	}**/

	@Override
	public int getBaseTier() {
		return 3;
	}

	@Override
	public boolean isChargeable() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}
}

