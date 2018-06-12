/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class AbilityWaterArc extends Ability {

	public AbilityWaterArc() {
		super(Waterbending.ID, "water_arc");
		requireRaytrace(-1, true);
	}

	@Override
	public void execute(AbilityContext ctx) {
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();

		Vector targetPos = getClosestWaterbendableBlock(entity, ctx.getLevel());

		if (targetPos != null || ctx.consumeWater(1)) {

			if (targetPos == null) {
				targetPos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(4));
			}
			float damageMult = 1F;
			int comboNumber = 1;
			//The water arc number in the combo.

			if (ctx.getLevel() == 1) {
				damageMult = 1.25F;
			}
			if (ctx.getLevel() == 2) {
				damageMult = 1.5F;
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				damageMult = 3F;
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				damageMult = 1 + comboNumber / 2;
			}

			if (bender.consumeChi(STATS_CONFIG.chiWaterArc)) {

				removeExisting(ctx);
				damageMult *= ctx.getPowerRatingDamageMod();

				if (!ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
					EntityWaterArc water = new EntityWaterArc(world);
					water.setOwner(entity);
					water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
					water.setDamageMult(damageMult);
					water.setBehavior(new WaterArcBehavior.PlayerControlled());
					water.isSpear(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
					world.spawnEntity(water);
					ctx.getData().addStatusControl(StatusControl.THROW_WATER);
				} else {
					Vector look = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(4));
					Vector force = Vector.toRectangular(Math.toRadians(entity.rotationYaw), Math.toRadians(entity.rotationPitch));
					force = force.times(15 + comboNumber);
					EntityWaterArc water = new EntityWaterArc(world);
					water.setOwner(entity);
					water.setPosition(look.x(), entity.getEyeHeight(), look.z());
					water.setDamageMult(damageMult);
					water.addVelocity(force);
					water.setBehavior(new WaterArcBehavior.Thrown());
				}

			}
		}
	}

	private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterArcSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterArcAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterArcAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterArcAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterArcAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> state.getBlock() == Blocks.WATER
						|| state.getBlock() == Blocks.FLOWING_WATER;

				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

	}


	//For bending snow and ice; is a separate method so that when passives are active it's easy to differentiate
	private Vector getClosestWaterbendableBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterArcSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterArcAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterArcAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterArcAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterArcAngles;

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


	/**
	 * Kills already existing water arc if there is one
	 */
	private void removeExisting(AbilityContext ctx) {

		EntityWaterArc water = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityWaterArc
				.class, ctx.getBenderEntity());

		if (water != null) {
			water.setBehavior(new WaterArcBehavior.Thrown());
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiWaterArc(this, entity, bender);
	}

}
