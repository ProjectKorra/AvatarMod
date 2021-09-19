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

package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar, FavouriteDragon
 * <p>
 * Flow Control!
 * <p>
 * Creates a multi-use bubble of water. Right click to shield, left click to lob.
 * Shift to circle it around you. Left click while circling to throw.
 * Can be used as a water source for other moves.
 * <p>
 * Level 3: Take the water bubble from any water source around you.
 * Level 4 Path 1: Holding shift charges it up and makes it spin incredibly fast. Left click to throw it,
 * making a massive water explosion.
 * Level 4 Path 2: Significantly increased health; while holding shift, right click to make
 * an encompassing shield around you.
 */
public class AbilityFlowControl extends Ability {

    public static final String
            RING = "ring",
            INFINITE_WATER = "infiniteWater";

    public AbilityFlowControl() {
        super(Waterbending.ID, "flow_control");
        requireRaytrace(-1, false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(SOURCE_ANGLES, SOURCE_RANGE, EXPLOSION_SIZE, EXPLOSION_DAMAGE, EFFECT_RADIUS, MAX_HEALTH);
        addBooleanProperties(RING, INFINITE_WATER);
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
        AbilityData abilityData = ctx.getAbilityData();

        if (ctx.consumeWater(getProperty(WATER_AMOUNT, ctx).intValue())) {
            int lifeTime = getProperty(LIFETIME, ctx).intValue();
            float damage = getProperty(DAMAGE, ctx).floatValue();
            float xp = getProperty(XP_HIT, ctx).floatValue();
            int waterLevel = getProperty(WATER_LEVEL, ctx).intValue();
            float size = getProperty(SIZE, ctx).floatValue();
            float chiHit = getProperty(CHI_HIT, ctx).floatValue();
            int performance = getProperty(PERFORMANCE, ctx).intValue();

            lifeTime = (int) powerModify(lifeTime, abilityData);
            damage = powerModify(damage, abilityData);
            size = powerModify(size, abilityData);

            EntityWaterBubble bubble = new EntityWaterBubble(world);
            bubble.setLifeTime(lifeTime);
            bubble.setDamage(damage);
            bubble.setTier(getCurrentTier(ctx));
            bubble.setOwner(entity);
            bubble.setEntitySize(size);
            bubble.setHealth(waterLevel);
            bubble.setXp(xp);
            bubble.setChiHit(chiHit);
        }
        super.execute(ctx);

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
