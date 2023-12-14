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
import com.crowsofwar.avatar.entity.AvatarEntity;
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
 * @author CrowsOfWar
 *
 * Creates a water arc. Wow. Amazing.
 * Press shift to 'bind' it to you, allowing you to use it as a whip.
 * Middle click to change from flow control to water arc.w
 */
public class AbilityWaterArc extends Ability {

    public static final String WATER_HITS = "waterHits";
    public AbilityWaterArc() {
        super(Waterbending.ID, "water_arc");
        requireRaytrace(-1, true);
    }

    @Override
    public void init() {
        super.init();
       addProperties(WATER_LEVEL, EXPLOSION_SIZE, EXPLOSION_DAMAGE, EFFECT_RADIUS, CHARGE_FREQUENCY,
                CHARGE_AMOUNT, EFFECT_RADIUS, RANGE, WATER_HITS);
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
            //Also need to implement health???
            int waterLevel = getProperty(WATER_LEVEL, ctx).intValue();
            float size = getProperty(SIZE, ctx).floatValue();
            float chiHit = getProperty(CHI_HIT, ctx).floatValue();
            int performance = getProperty(PERFORMANCE, ctx).intValue();
            float swirlRadius = getProperty(EFFECT_RADIUS, ctx).floatValue();
            float distance = getProperty(RANGE, ctx).floatValue();

            BlockPos spawnPos = abilityData.getSourceInfo().getBlockPos();

            lifeTime = (int) powerModify(lifeTime, abilityData);
            damage = powerModify(damage, abilityData);
            size = powerModify(size, abilityData);
            swirlRadius = powerModify(swirlRadius, abilityData);
            distance = powerModify(distance, abilityData);

            EntityWaterBubble existing = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class,
                    entity);
            if (existing == null) {
                EntityWaterBubble bubble = new EntityWaterBubble(world);
                bubble.setLifeTime(lifeTime);
                bubble.setDamage(damage);
                bubble.setTier(getCurrentTier(ctx));
                bubble.setOwner(entity);
                //Grow the bubble with an appear animation
                bubble.setEntitySize(0.05F);
                bubble.setMaxEntitySize(size);
                bubble.setMaxSize(size);
                bubble.setMaxHealth(waterLevel);
                bubble.setHealth(waterLevel);
                bubble.setXp(xp);
                bubble.setChiHit(chiHit);
                bubble.setBehaviour(new WaterBubbleBehavior.Grow());
                bubble.setPerformanceAmount(performance);
                bubble.setAbility(this);
                bubble.setPosition(spawnPos.getX(), spawnPos.getY() + 0.5, spawnPos.getZ());
                bubble.setState(EntityWaterBubble.State.ARC);
                bubble.setDefaultState(EntityWaterBubble.State.ARC);
                bubble.setDegreesPerSecond(size * 2);
                bubble.setSwirlRadius(swirlRadius);
                bubble.setDistance(distance);
                bubble.setDamageSource("avatar_Water");
                bubble.setPiercing(false);
                bubble.setHits(getProperty(WATER_HITS, ctx).intValue());

                //Only want to spawn it server side
                if (!world.isRemote)
                    world.spawnEntity(bubble);

                //Add all status controls except for throw.
                //Replace lob w/ throw while holding shift.
                data.addStatusControl(StatusControlController.LOB_BUBBLE);
                data.addStatusControl(StatusControlController.SHIELD_BUBBLE);
                data.addStatusControl(StatusControlController.SWIRL_BUBBLE);
                data.addStatusControl(StatusControlController.MODIFY_WATER);
            }
        }
        else {
            bender.sendMessage("avatar.waterSourceFail");
        }
        super.execute(ctx);

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
