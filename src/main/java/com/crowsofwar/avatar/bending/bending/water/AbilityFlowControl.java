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
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        addProperties(WATER_LEVEL, EXPLOSION_SIZE, EXPLOSION_DAMAGE, EFFECT_RADIUS, MAX_HEALTH);
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

            BlockPos spawnPos = abilityData.getSourceInfo().getBlockPos();

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
            bubble.setBehaviour(new WaterBubbleBehavior.PlayerControlled());
            bubble.setPerformanceAmount(performance);
            bubble.setAbility(this);
            bubble.setPosition(spawnPos.getX(), spawnPos.getY() + 0.5, spawnPos.getZ());
            bubble.setState(EntityWaterBubble.State.BUBBLE);
            bubble.setDegreesPerSecond(size * 2);

            //Only want to spawn it server side
            if (!world.isRemote)
                world.spawnEntity(bubble);

            //Add all status controls except for throw.
            //Replace lob w/ throw while holding shift.
            data.addStatusControl(StatusControlController.LOB_BUBBLE);
        }
        super.execute(ctx);

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
