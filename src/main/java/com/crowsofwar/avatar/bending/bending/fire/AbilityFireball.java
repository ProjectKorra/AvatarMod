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
package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

/**
 * @author CrowsOfWar
 */
public class AbilityFireball extends Ability {

    public static final String FIREBALLS = "fireballs";

    public AbilityFireball() {
        super(Firebending.ID, "fireball");
        requireRaytrace(2.5, false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B, EXPLOSION_SIZE, EXPLOSION_DAMAGE,
                MAX_BURNOUT, MAX_DAMAGE, MAX_SIZE, MAX_EXHAUSTION, FIREBALLS);
    }

    //We want these to be applied manually upon executing the status control.

    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getBurnOut(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getExhaustion(AbilityContext ctx) {
        return 0;
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        AbilityData abilityData = ctx.getAbilityData();


        if (bender.consumeChi(getChiCost(ctx) / 4f)) {

            Vector target;
            if (ctx.isLookingAtBlock()) {
                target = Raytrace.getTargetBlock(entity, 2.5).getPosPrecise();
            } else {
                Vector playerPos = getEyePos(entity);
                target = playerPos.plus(getLookRectangular(entity).times(2.5));
            }

            int r, g, b, fadeR, fadeG, fadeB;
            float damage = getProperty(DAMAGE, ctx).floatValue();
            float size = getProperty(SIZE, ctx).floatValue();
            int lifetime = getProperty(LIFETIME, ctx).intValue();
            int fireballAmount = getProperty(FIREBALLS, ctx).intValue();
            int performance = getProperty(PERFORMANCE, ctx).intValue();
            int fireTime = getProperty(FIRE_TIME, ctx).intValue();
            float chiHit = getProperty(CHI_HIT, ctx).floatValue();
            float explosionSize = getProperty(EXPLOSION_SIZE, ctx).floatValue();
            float explosionDamage = getProperty(EXPLOSION_DAMAGE, ctx).floatValue();
            r = getProperty(FIRE_R, ctx).intValue();
            g = getProperty(FIRE_G, ctx).intValue();
            b = getProperty(FIRE_B, ctx).intValue();
            fadeR = getProperty(FADE_R, ctx).intValue();
            fadeG = getProperty(FADE_G, ctx).intValue();
            fadeB = getProperty(FADE_B, ctx).intValue();

            boolean canUse = !data.hasStatusControl(THROW_FIREBALL);

            List<EntityFireball> fireballs = world.getEntitiesWithinAABB(EntityFireball.class,
                    entity.getEntityBoundingBox().grow(3.5, 3.5, 3.5));
            fireballs = fireballs.stream().filter(entityFireball -> entityFireball.getOwner() == entity).collect(Collectors.toList());
            canUse |= fireballs.size() < fireballAmount;

            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage += size;
            explosionSize *= abilityData.getDamageMult() * abilityData.getXpModifier();
            explosionDamage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            chiHit *= abilityData.getDamageMult();
            lifetime *= (0.75 + 0.25 * abilityData.getDamageMult() * abilityData.getXpModifier());
            // System.out.println(size);

            if (canUse) {
                assert target != null;
                EntityFireball fireball = new EntityFireball(world);
                fireball.setPosition(target);
                fireball.setOwner(entity);
                fireball.setBehaviour(new FireballBehavior.PlayerControlled());
                fireball.setDamage(damage);
                fireball.setPowerRating(bender.calcPowerRating(Firebending.ID));
                fireball.setEntitySize(size);
                fireball.setLifeTime(lifetime);
                fireball.setPerformanceAmount(performance);
                fireball.setAbility(this);
                fireball.setChiHit(chiHit);
                fireball.setTier(getCurrentTier(ctx));
                fireball.setExplosionDamage(explosionDamage);
                fireball.setExplosionSize(explosionSize);
                fireball.setFireTime(fireTime);
                fireball.setDamageSource("avatar_Fire_fireball");
                fireball.setRGB(r, g, b);
                fireball.setRedirectable(true);
                fireball.setFade(fadeR, fadeG, fadeB);
                fireball.setXp(getProperty(XP_HIT, ctx).floatValue());
                if (!world.isRemote)
                    world.spawnEntity(fireball);

                abilityData.setRegenBurnout(false);
                data.addStatusControl(THROW_FIREBALL);

            }

        }

    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiFireball(this, entity, bender);
    }

    @Override
    public int getBaseTier() {
        return 3;
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
