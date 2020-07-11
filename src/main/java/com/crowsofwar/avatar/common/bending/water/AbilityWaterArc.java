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
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_WATER;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;
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
        BendingData data = ctx.getData();

        Vector targetPos = getClosestWaterbendableBlock(entity, ctx.getLevel() * 2);


        if (targetPos != null || ctx.consumeWater(1) || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())) {

            if (targetPos == null) {
                targetPos = Vector.getEyePos(entity).plus(getLookRectangular(entity).times(2.5));
            }
            if (targetPos != null && (entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative())) {
                world.setBlockToAir(targetPos.toBlockPos());
            }


            float damageMult = 1F;
            float gravity = 8;
            float size = 0.5F;
            //The water arc number in the combo.

            if (ctx.getLevel() == 1) {
                damageMult = 1.25F;
                gravity = 7.5F;
                size = 0.65F;
            }
            if (ctx.getLevel() == 2) {
                damageMult = 1.5F;
                gravity = 7;
                size = 0.8F;
            }
            if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
                damageMult = 3F;
                gravity = 3;
                size = 0.4F;
            }
            if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
                gravity = 9.81F;
                size = 0.5F;
            }

            if (bender.consumeChi(STATS_CONFIG.chiWaterArc)) {

                removeExisting(ctx);

                EntityWaterArc water = new EntityWaterArc(world);
                water.setOwner(entity);
                assert targetPos != null;
                water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
                water.setDamageMult(damageMult);
                water.setEntitySize(size);
                water.setDamage(damageMult * STATS_CONFIG.waterArcSettings.damage);
                water.setXp(SKILLS_CONFIG.waterHit);
                water.setTier(getCurrentTier(ctx));
                water.setLifeTime(30);
                water.setBehavior(new WaterArcBehavior.PlayerControlled());
                water.isSpear(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND));
                water.setGravity(gravity);
                water.setAbility(this);
                if (!world.isRemote)
                    world.spawnEntity(water);
                ctx.getData().addStatusControl(THROW_WATER);


            }
        }
    }

	/*private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
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

    //For bending snow and ice; is a separate method so that when passives are active it's easy to differentiate
    //For some reason this doesn't work; will use alternate method for now
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

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
