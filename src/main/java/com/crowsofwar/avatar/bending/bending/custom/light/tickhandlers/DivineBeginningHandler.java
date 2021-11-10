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
package com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.light.AbilityDivineBeginning;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.blocks.BlockUtils;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_DIVINE_BEGINNING;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_HOLY_PROTECTION;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class DivineBeginningHandler extends TickHandler {

    public static final UUID DIVINE_BEGINNING_MOVEMENT_ID = UUID.randomUUID();

    public DivineBeginningHandler(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("divine_beginning");
        AbilityDivineBeginning divineBeginning = (AbilityDivineBeginning) Abilities.get("divine_beginning");
        if (divineBeginning == null)
            return false;

        float requiredChi = divineBeginning.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;

        if (bender.consumeChi(requiredChi) && data.hasStatusControl(RELEASE_DIVINE_BEGINNING)) {

            Vector eye = getEyePos(entity);
//            boolean isRaining = world.isRaining() && world.canSeeSky(entity.getPosition()) && world.getBiome(entity.getPosition()).canRain();
//            boolean inWaterBlock = world.getBlockState(entity.getPosition()) instanceof BlockLiquid || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition()).getBlock() == Blocks.FLOWING_WATER;
//            boolean headInLiquid = world.getBlockState(entity.getPosition().up()) instanceof BlockLiquid || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.FLOWING_WATER;


            double speedMult = divineBeginning.getProperty(SPEED, abilityData).floatValue() * 3;
            double randomness = 0;
            float size = divineBeginning.getProperty(SIZE, abilityData).floatValue();
            float damage = divineBeginning.getProperty(DAMAGE, abilityData).floatValue();
            float performanceAmount = divineBeginning.getProperty(PERFORMANCE, abilityData).floatValue();
            float xp = divineBeginning.getProperty(XP_HIT, abilityData).floatValue();
            int lifetime = divineBeginning.getProperty(LIFETIME, abilityData).intValue();
            float knockback = divineBeginning.getProperty(KNOCKBACK, abilityData).floatValue();

            //RGB values for being kewl
            int r, g, b, fadeR, fadeG, fadeB;
            r = divineBeginning.getProperty(R, abilityData).intValue();
            g = divineBeginning.getProperty(G, abilityData).intValue();
            b = divineBeginning.getProperty(B, abilityData).intValue();
            fadeR = divineBeginning.getProperty(FADE_R, abilityData).intValue();
            fadeG = divineBeginning.getProperty(FADE_G, abilityData).intValue();
            fadeB = divineBeginning.getProperty(FADE_B, abilityData).intValue();


            // Affect stats by power rating
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            speedMult *= abilityData.getDamageMult() * abilityData.getXpModifier();
            randomness -= bender.calcPowerRating(Firebending.ID) / 100;
            randomness *= (0.5 / abilityData.getPowerRatingMult()) * abilityData.getXpModifier();
            randomness = randomness < 0 ? 0 : randomness;
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();


            double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
            double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
            double range = Math.min(Math.max(1F - speedMult / 25, 0.1F), 0.25F);
            Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
            Vector start = look.times(range).plus(eye.minusY(0.45));


            //Raytrace for the beam; will do collision later
            //Raytrace collision too
            //Also need snowflake particles

            RayTraceResult res = Raytrace.rayTrace(world, start.toMinecraft(), start.plus(look.times(lifetime)).toMinecraft(), size,
                    true, true, false, Entity.class, Objects::isNull);
            double distance = lifetime;
            if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK)
                distance = start.toMinecraft().distanceTo(res.hitVec);

            //Sets fire on the ground, tries along 3 positions
            for (int i = 0; i < distance; i++) {
                BlockPos pos = start.plus(look.times(i)).toBlockPos();
                for (int h = 0; h < 5; h++) {
                    pos = pos.down(h);
                    if (AvatarUtils.getRandomNumberInRange(1, 10) <= 6) {
                        if (BlockUtils.canPlaceFireAt(world, pos)) {
                            BlockTemp.createTempBlock(world, pos, 60, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
            //Hit Entities
            List<Entity> targets = Raytrace.entityRaytrace(world, start,
                    look, distance, size * 1.25F, entity1 ->
                            DamageUtils.canDamage(entity, entity1));

            //Damage and knockback
            for (Entity hit : targets) {
                if (hit != entity) {
                    if (!world.isRemote) {
                        DamageUtils.attackEntity(entity, hit,
                                AvatarDamageSource.LIGHT, damage, (int) performanceAmount,
                                divineBeginning, xp);
                        Vector vel = look.times(speedMult / 80 * knockback);
                        hit.addVelocity(vel.x(), vel.y(), vel.z());
                        AvatarUtils.afterVelocityAdded(hit);
                        hit.isAirBorne = true;
                    }
                    //Maybe slow targets?
                }
            }


            //Visuals
            int particles = (int) (distance / lifetime * 90);
            //Particle code.
            if (world.isRemote) {
                float radius = 1.5F;
                for (int i = 0; i < 360; i += 60) {
                    //We only want 6 orbs
                    //Create the points
                    double angle = Math.toRadians(i);
                    double x = radius * Math.cos(angle);
                    double y = 0;
                    double z = radius * Math.sin(angle);
                    Vector pos = new Vector(x, y, z);
                    pos = Vector.rotateAroundAxisX(pos, entity.rotationPitch + 90);
                    pos = Vector.rotateAroundAxisY(pos, entity.rotationYaw);
                    pos = pos.plus(Vector.getEyePos(entity).minusY(0.45));


                    //With pos as our new vector, we make spheres.
                    if (entity.ticksExisted % 6 == 0 || data.getTickHandlerDuration(this) == 1) {
                        double x1, y1, z1;
                        for (double theta = 0; theta <= 180; theta += 1) {
                            double dphi = (90) / Math.sin(Math.toRadians(theta));
                            for (double phi = 0; phi < 360; phi += dphi) {
                                double rphi = Math.toRadians(phi);
                                double rtheta = Math.toRadians(theta);

                                //Making it spin increases the sphere size
                                x1 = radius / 4 * Math.cos(rphi) * Math.sin(rtheta);
                                y1 = radius / 4 * Math.sin(rphi) * Math.sin(rtheta);
                                z1 = radius / 4 * Math.cos(rtheta);

                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(look.x() * 0.0125 + world.rand.nextGaussian() / 80,
                                                look.z() * 0.0125 + world.rand.nextGaussian() / 80, look.z() * 0.0125 + world.rand.nextGaussian() / 80).
                                        scale(radius / 4 * 0.85F)
                                        .time(12).pos(pos.toMinecraft().add(look.times(0.75).toMinecraft().add(x1, y1 - 0.025, z1))).spin(0.1, world.rand.nextGaussian() / 20)
                                        .clr(1F, 1F, 0.3F, 0.025F).glow(true).spawnEntity(entity).spawn(world);
                            }
                        }
                    }

                    Vector targetPos = look.times(3).plus(Vector.getEyePos(entity).minusY(0.45));
                    //Beam trail of particles
                    Vector vel = targetPos.minus(pos);
                    //Makes sure the beam doesn't appear before the sphere
                    for (int h = 0; h < 6; h++) {
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(vel.x() * 0.125,
                                        vel.y() * 0.125, vel.z() * 0.125).
                                scale(size * 1.5F)
                                .time(12).pos(pos.toMinecraft())
                                .clr(1F, 1F, 0.3F, 0.085F).glow(true).spawnEntity(entity).spawn(world);

                    }
                }
                //Bruh coloured lighting disables the beam
                //Beam from the player's chest
//                ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(start.toMinecraft())
//                        .target(start.plus(look.times(distance)).toMinecraft()).scale(size * 5F).time(1)
//                        .clr(7 + AvatarUtils.getRandomNumberInRange(0, 10),
//                                11 + AvatarUtils.getRandomNumberInRange(0, 10),
//                                40 + AvatarUtils.getRandomNumberInRange(0, 10), 5).collide(true).spawn(world);
                //Flash particles
                AvatarUtils.spawnDirectionalHelix(world, entity, look.toMinecraft(), particles, distance, size * 0.75,
                        ParticleBuilder.Type.FLASH, start.toMinecraft(), look.toMinecraft(),
                        true, 12, true, 1.0F, 1.0F, 0.3F, 0.015F, size * 0.95F);

            }

            if (ctx.getData().getTickHandlerDuration(this) % 4 == 0)
                world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_GLASS_STEP,
                        SoundCategory.PLAYERS, 0.4f, 0.8f);

            float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
            if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOVEMENT_ID) == null)
                applyMovementModifier(entity, movementModifier);

            entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                    0.6F, 0.8F + world.rand.nextFloat() / 10);


        } else {
            if (!data.hasStatusControl(RELEASE_HOLY_PROTECTION)) {
                // not enough chi
                //makes sure the tick handler is removed

            }

            return true;
        }
        return !data.hasStatusControl(RELEASE_DIVINE_BEGINNING);
    }


    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("divine_beginning");
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOVEMENT_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(DIVINE_BEGINNING_MOVEMENT_ID);
        abilityData.setRegenBurnout(true);

    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(DIVINE_BEGINNING_MOVEMENT_ID);

        moveSpeed.applyModifier(new AttributeModifier(DIVINE_BEGINNING_MOVEMENT_ID, "Divine Beginning Movement Modifier", multiplier - 1, 1));

    }

}
