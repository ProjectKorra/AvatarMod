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
package com.crowsofwar.avatar.bending.bending.custom.abyss.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.abyss.AbilityAbyssalBeam;
import com.crowsofwar.avatar.bending.bending.custom.abyss.Abyssbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower.RANDOMNESS;
import static com.crowsofwar.avatar.util.data.StatusControlController.SHOOT_HYPER_BEAM;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class HyperBeamHandler extends TickHandler {

    public static final UUID HYPER_BEAM_MOVEMENT_MOD_ID = UUID.randomUUID();

    public HyperBeamHandler(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("hyper_beam");
        AbilityAbyssalBeam hyperBeam = (AbilityAbyssalBeam) Abilities.get("hyper_beam");
        if (hyperBeam == null)
            return false;

        float requiredChi = hyperBeam.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;

        if (bender.consumeChi(requiredChi) && data.hasStatusControl(SHOOT_HYPER_BEAM)) {

            Vector eye = getEyePos(entity);
//            boolean isRaining = world.isRaining() && world.canSeeSky(entity.getPosition()) && world.getBiome(entity.getPosition()).canRain();
//            boolean inWaterBlock = world.getBlockState(entity.getPosition()) instanceof BlockLiquid || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition()).getBlock() == Blocks.FLOWING_WATER;
//            boolean headInLiquid = world.getBlockState(entity.getPosition().up()) instanceof BlockLiquid || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.FLOWING_WATER;


            double speedMult = hyperBeam.getProperty(SPEED, abilityData).floatValue() * 3;
            double randomness = hyperBeam.getProperty(RANDOMNESS, abilityData).doubleValue();
            float size = hyperBeam.getProperty(SIZE, abilityData).floatValue();
            float damage = hyperBeam.getProperty(DAMAGE, abilityData).floatValue();
            float performanceAmount = hyperBeam.getProperty(PERFORMANCE, abilityData).floatValue();
            float xp = hyperBeam.getProperty(XP_HIT, abilityData).floatValue();
            int lifetime = hyperBeam.getProperty(LIFETIME, abilityData).intValue();
            float knockback = hyperBeam.getProperty(KNOCKBACK, abilityData).floatValue();



            // Affect stats by power rating
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            speedMult *= abilityData.getDamageMult() * abilityData.getXpModifier();
            randomness -= bender.calcPowerRating(Abyssbending.ID) / 100;
            randomness *= (0.5 / abilityData.getPowerRatingMult()) * abilityData.getXpModifier();
            randomness = randomness < 0 ? 0 : randomness;
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();


            double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
            double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
            double range = Math.min(Math.max(1F - speedMult / 25, 0.1F), 0.25F);
            Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
            Vector start = look.times(range).plus(eye.minusY(0.45));

            //Instead of start being at the player's eyes, we want it at their hands:
            //copied from flame strike
            Vec3d height, rightSide;
            if (entity instanceof EntityPlayer) {
                if (!AvatarMod.realFirstPersonRender2Compat && (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1)) {
                    height = entity.getPositionVector().add(0, 1.5, 0);
                    height = height.add(entity.getLookVec().scale(0.8));
                    //Right
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                    //Left
                    else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                } else {
                    height = entity.getPositionVector().add(0, 0.84, 0);
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                    } else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                    }
                }
            } else {
                height = entity.getPositionVector().add(0, 0.84, 0);
                if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                } else {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                }

            }
            rightSide = rightSide.add(height);
            start = Vector.fromVec3d(rightSide);


            //Raytrace for the beam; will do collision later
            //Raytrace collision too
            //Also need snowflake particles

            RayTraceResult res = Raytrace.rayTrace(world, start.toMinecraft(), start.plus(look.times(lifetime)).toMinecraft(), size,
                    true, true, false, Entity.class, Objects::isNull);
            double distance = lifetime;
            if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK)
                distance = start.toMinecraft().distanceTo(res.hitVec);

            //Hit Entities
            List<Entity> targets = Raytrace.entityRaytrace(world, start,
                    look, distance, size * 1.25F, entity1 ->
                            DamageUtils.canDamage(entity, entity1));

            //Damage and knockback
            for (Entity hit : targets) {
                if (hit != entity) {
                    if (!world.isRemote) {
                        DamageUtils.attackEntity(entity, hit,
                                AvatarDamageSource.ENERGY, damage, (int) performanceAmount,
                                hyperBeam, xp);
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
                //Bruh coloured lighting disables the beam
                //Beam from the player's chest
                ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(start.toMinecraft())
                        .target(start.plus(look.times(distance)).toMinecraft()).scale(size * 8F).time(3)
                        .clr(getClrRand(), getClrRand(), getClrRand(), 5 / 255F).spawn(world);
                AvatarUtils.spawnDirectionalHelix(world, entity, look.toMinecraft(), particles * 4, distance, size * 0.75,
                        ParticleBuilder.Type.FLASH, start.toMinecraft(), look.toMinecraft(),
                        true, 8, true, getClrRand(), getClrRand(), getClrRand(), 0.8F, size * 0.75F);
                AvatarUtils.spawnDirectionalHelix(world, entity, look.toMinecraft(), particles * 2, distance, size * 0.75,
                        ParticleBuilder.Type.FLASH, start.toMinecraft(), look.toMinecraft(),
                        true, 8, true, getClrRand(), getClrRand(), getClrRand(), 0.15F, size * 0.75F);


                //Particles at the beginning of the beam
                //TODO: Big sphere of particles flowing forwards

                if (entity.ticksExisted % 4 == 0 || data.getTickHandlerDuration(this) == 1) {
                    double x1, y1, z1;
                    for (double theta = 0; theta <= 180; theta += 1) {
                        double dphi = (56 - size * 6) / Math.sin(Math.toRadians(theta));
                        for (double phi = 0; phi < 360; phi += dphi) {
                            double rphi = Math.toRadians(phi);
                            double rtheta = Math.toRadians(theta);

                            //Making it spin increases the sphere size
                            x1 = size * 1.25F * Math.cos(rphi) * Math.sin(rtheta);
                            y1 = size * 1.25F * Math.sin(rphi) * Math.sin(rtheta);
                            z1 = size * 1.25F * Math.cos(rtheta);

                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(look.x() * 0.0125 + world.rand.nextGaussian() / 80,
                                            look.z() * 0.0125 + world.rand.nextGaussian() / 80, look.z() * 0.0125 + world.rand.nextGaussian() / 80).
                                    scale(size * 0.65F).element(BendingStyles.get(Firebending.ID))
                                    .time(8).pos(start.toMinecraft().add(look.times(0.05).toMinecraft().add(x1, y1 - 0.025, z1))).spin(0.1, world.rand.nextGaussian() / 20)
                                    .clr(getClrRand(), getClrRand(), getClrRand(), 0.85F).glow(true).spawnEntity(entity).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(look.x() * 0.0125 + world.rand.nextGaussian() / 80,
                                            look.z() * 0.0125 + world.rand.nextGaussian() / 80, look.z() * 0.0125 + world.rand.nextGaussian() / 80).
                                    scale(size * 0.65F)
                                    .time(8).pos(start.toMinecraft().add(look.times(0.05).toMinecraft().add(x1, y1 - 0.025, z1))).spin(0.1, world.rand.nextGaussian() / 20)
                                    .clr(getClrRand(), getClrRand(), getClrRand(), 0.15F).glow(true).spawnEntity(entity).spawn(world);

                        }
                    }
                }
            }

            if (ctx.getData().getTickHandlerDuration(this) % 4 == 0)
                world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_GLASS_STEP,
                        SoundCategory.PLAYERS, 0.4f, 0.8f);

            float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
            if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HYPER_BEAM_MOVEMENT_MOD_ID) == null)
                applyMovementModifier(entity, movementModifier);

            entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                    0.6F, 0.8F + world.rand.nextFloat() / 10);


        } else {

            return true;
        }
        return !data.hasStatusControl(SHOOT_HYPER_BEAM);
    }

    private float getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 255) / 255F;
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("hyper_beam");
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HYPER_BEAM_MOVEMENT_MOD_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HYPER_BEAM_MOVEMENT_MOD_ID);
        abilityData.setRegenBurnout(true);

    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(HYPER_BEAM_MOVEMENT_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(HYPER_BEAM_MOVEMENT_MOD_ID, "Hyper Beam Movement Modifier", multiplier - 1, 1));

    }

}
