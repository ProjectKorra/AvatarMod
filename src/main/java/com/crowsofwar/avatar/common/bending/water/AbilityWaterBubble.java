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
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.LOB_BUBBLE;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class AbilityWaterBubble extends Ability {

	public AbilityWaterBubble() {
		super(Waterbending.ID, "water_bubble");
		requireRaytrace(-1, false);
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		Vector targetPos = getClosestWaterbendableBlock(entity, ctx.getLevel() * 2);

		if (ctx.isLookingAtBlock()) {
			BlockPos lookPos = ctx.getLookPosI().toBlockPos();
			IBlockState lookingAtBlock = world.getBlockState(lookPos);
			if (STATS_CONFIG.waterBendableBlocks.contains(lookingAtBlock.getBlock())
					|| STATS_CONFIG.plantBendableBlocks.contains(lookingAtBlock.getBlock())) {

				if (bender.consumeChi(STATS_CONFIG.chiWaterBubble)) {

					EntityWaterBubble existing = AvatarEntity.lookupEntity(world, EntityWaterBubble.class, //
							bub -> bub.getBehavior() instanceof WaterBubbleBehavior.PlayerControlled
									&& bub.getOwner() == entity);

					if (existing != null) {
						existing.setBehavior(new WaterBubbleBehavior.Drop());
						// prevent bubble from removing status control
						existing.setOwner(null);
					}

					Vector pos = ctx.getLookPos();

					EntityWaterBubble bubble = new EntityWaterBubble(world);
					assert pos != null;
					bubble.setPosition(pos.x(), pos.y(), pos.z());
					bubble.setBehavior(new WaterBubbleBehavior.PlayerControlled());
					bubble.setOwner(entity);
					bubble.setSourceBlock(ctx.getLevel() >= 2);
					bubble.setAbility(this);

					// Workaround to fix issue where water bubble gets destroyed quickly after creation
					// This is because the water bubble is destroyed once it's inside water, and after being created,
					// the water quickly surrounds and destroys it
					// This will allow the bubble to travel out of the way of the water before it gets destroyed
					bubble.setVelocity(Vector.UP);

					if (!world.isRemote)
						world.spawnEntity(bubble);

					data.addStatusControl(LOB_BUBBLE);
					//data.addStatusControl(StatusControl.CHARGE_BUBBLE);
					data.getAbilityData(this).addXp(SKILLS_CONFIG.createBubble);

					if (!ctx.isMasterLevel(AbilityTreePath.SECOND)) {
						world.setBlockToAir(lookPos);
					}

				}
			}
		} /*else if (targetPos != null) {
			if (bender.consumeChi(STATS_CONFIG.chiWaterBubble)) {

				EntityWaterBubble existing = AvatarEntity.lookupEntity(world, EntityWaterBubble.class, //
						bub -> bub.getBehavior() instanceof WaterBubbleBehavior.PlayerControlled
								&& bub.getOwner() == entity);

				if (existing != null) {
					existing.setBehavior(new WaterBubbleBehavior.Drop());
					// prevent bubble from removing status control
					existing.setOwner(null);
				}

				Vector pos = Vector.getLookRectangular(entity).times(1.5);

				EntityWaterBubble bubble = new EntityWaterBubble(world);
				bubble.setPosition(pos);
				bubble.setBehavior(new WaterBubbleBehavior.PlayerControlled());
				bubble.setOwner(entity);
				bubble.setSourceBlock(ctx.getLevel() >= 2);
				bubble.setAbility(this);

				// Workaround to fix issue where water bubble gets destroyed quickly after creation
				// This is because the water bubble is destroyed once it's inside water, and after being created,
				// the water quickly surrounds and destroys it
				// This will allow the bubble to travel out of the way of the water before it gets destroyed
				bubble.setVelocity(Vector.UP);

				world.spawnEntity(bubble);

				data.addStatusControl(StatusControl.THROW_BUBBLE);
				data.getAbilityData(this).addXp(SKILLS_CONFIG.createBubble);

				if (!ctx.isMasterLevel(AbilityTreePath.SECOND)) {
					world.setBlockToAir(targetPos.toBlockPos());
				}

			}
		}**/
	}

	private Vector getClosestWaterbendableBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.waterBubbleSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterBubbleAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterBubbleAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterBubbleAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterBubbleAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> (STATS_CONFIG.waterBendableBlocks.contains(state.getBlock())
						|| STATS_CONFIG.plantBendableBlocks.contains(state.getBlock())) && state.getBlock() != Blocks.AIR;


				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

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
