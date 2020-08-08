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
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

/**
 * @author CrowsOfWar
 */
public class AbilityFireball extends Ability {


    public AbilityFireball() {
        super(Firebending.ID, "fireball");
        requireRaytrace(2.5, false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B, EXPLOSION_SIZE, EXPLOSION_DAMAGE,
                MAX_BURNOUT, MAX_DAMAGE, MAX_SIZE, MAX_EXHAUSTION);
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
            canUse |= fireballs.size() < 3 && ctx.isDynamicMasterLevel(AbilityTreePath.FIRST);

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
                fireball.setBehaviour(fireballs.size() < 1 ? new FireballOrbitController() : new FireballBehavior.PlayerControlled());
                fireball.setDamage(damage);
                fireball.setPowerRating(bender.calcPowerRating(Firebending.ID));
                fireball.setEntitySize(size / 2F);
                fireball.setLifeTime(lifetime);
                fireball.setOrbitID(fireballs.size() + 1);
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

    public static class FireballOrbitController extends FireballBehavior.PlayerControlled {

        @Override
        public FireballBehavior onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();

            if (owner == null || !(entity instanceof EntityFireball)) return this;

            BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

            Vector look = Vector.getLookRectangular(owner);
            Vector target = Vector.getEyePos(owner).plus(look.times(2 + ((EntityFireball) entity).getSize() * 0.03125F));
            List<EntityFireball> fireballs = entity.world.getEntitiesWithinAABB(EntityFireball.class,
                    owner.getEntityBoundingBox().grow(5, 5, 5));
            fireballs = fireballs.stream().filter(entityFireball -> entityFireball.getBehaviour() instanceof FireballBehavior.PlayerControlled
                    && entityFireball.getOwner() == entity.getOwner()).collect(Collectors.toList());
            Vec3d motion = Objects.requireNonNull(target).minus(Vector.getEntityPos(entity)).toMinecraft();

            if (!fireballs.isEmpty() && fireballs.size() > 1 && fireballs.contains(entity)) {
                //Ensures a constant list order for the fireballs
                int size = fireballs.size();
                int index = fireballs.indexOf(entity);
                int id = Math.max(((EntityFireball) entity).getOrbitID() - 1, 0);
                if (index != id) {
                    EntityFireball newBall = fireballs.get(id);
                    fireballs.set(fireballs.indexOf(newBall), (EntityFireball) entity);
                    fireballs.set(index, newBall);
                }
                int secondIn = 1;
                EntityFireball ball2nd = fireballs.get(1);
                int secondId = Math.max(ball2nd.getOrbitID() - 1, 0);
                if (secondIn != secondId) {
                    EntityFireball newBall = fireballs.get(secondId);
                    fireballs.set(fireballs.indexOf(newBall), ball2nd);
                    fireballs.set(secondIn, newBall);
                }
                int angle = (entity.getOwner().ticksExisted * 5) % 360;
                for (int i = 0; i < size; i++) {
                    //Tfw the game is adding an extra 120 degrees for no reason
                    angle = angle + (360 / size * i);
                    if (i == 2)
                        angle -= 120;
                    double radians = Math.toRadians(angle);
                    double x = 1.75 * Math.cos(radians);
                    double z = 1.75 * Math.sin(radians);
                    Vec3d pos = new Vec3d(x, 0, z);
                    pos = pos.add(owner.posX, owner.getEntityBoundingBox().minY + 1, owner.posZ);
                    motion = pos.subtract(fireballs.get(i).getPositionVector()).scale(0.75);
                    fireballs.get(i).setVelocity(motion);
                }
            } else {
                motion = motion.scale(0.75);
                entity.setVelocity(motion);
            }
            data.addStatusControl(THROW_FIREBALL);


            if (entity.getAbility() instanceof AbilityFireball) {
                AbilityFireball ball = (AbilityFireball) Abilities.get("fireball");
                if (ball != null) {
                    AbilityData abilityData = data.getAbilityData(ball);
                    float maxSize = ball.getProperty(MAX_SIZE, abilityData).floatValue();
                    maxSize *= (0.75 + 0.25 * abilityData.getDamageMult() * abilityData.getXpModifier());
                    if (maxSize > entity.getAvgSize()) {
                        if (entity.ticksExisted % 2 == 0) {
                            entity.setEntitySize((entity).getAvgSize() + 0.025F);
                        }
                    }
                }
            }

            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {

        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void load(NBTTagCompound nbt) {

        }

        @Override
        public void save(NBTTagCompound nbt) {

        }
    }


}
