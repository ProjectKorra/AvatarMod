package com.crowsofwar.avatar.bending.bending.combustion.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityMegatonDive;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

public class MegatonDiveHandler extends TickHandler {


    public MegatonDiveHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = data.getAbilityData("megaton_dive");
        AbilityMegatonDive dive = (AbilityMegatonDive) Abilities.get("megaton_dive");
        World world = ctx.getWorld();

        int duration = data.getTickHandlerDuration(this);
        int lifetime;

        if (dive != null) {
            lifetime = dive.getProperty(Ability.LIFETIME, abilityData).intValue();
            double targetSpeed = dive.getProperty(Ability.SPEED, abilityData).doubleValue();

            float size = dive.getProperty(Ability.SIZE, abilityData).floatValue() / 6;
            int rings = (int) (size * 6);
            //The size is the radius, circumference uses the diameter
            int particles = (int) (Math.PI * 2 * size) * 2;
            if (duration < lifetime) {

                if (entity.moveForward != 0) {
                    if (entity.moveForward < 0) {
                        targetSpeed /= 2;
                    } else {
                        targetSpeed *= 1.3;
                    }
                }


                Vector currentVelocity = new Vector(entity.motionX, entity.motionY, entity.motionZ);
                Vector targetVelocity = toRectangular(toRadians(entity.rotationYaw), 0).times(targetSpeed);

                double targetWeight = 0.1;
                currentVelocity = currentVelocity.times(1 - targetWeight);
                targetVelocity = targetVelocity.times(targetWeight);

                double targetSpeedWeight = 0.2;
                double speed = currentVelocity.magnitude() * (1 - targetSpeedWeight)
                        + targetSpeed * targetSpeedWeight;

                Vector newVelocity = currentVelocity.plus(targetVelocity).normalize().times(speed);

                Vector playerMovement = toRectangular(toRadians(entity.rotationYaw - 90),
                        toRadians(entity.rotationPitch)).times(entity.moveStrafing * 0.02);

                newVelocity = newVelocity.plus(playerMovement);

                entity.motionX = newVelocity.x();
                entity.motionY = 0;
                entity.motionZ = newVelocity.z();

            } else if (duration == lifetime) {
                entity.motionX = entity.getLookVec().x * targetSpeed;
                entity.motionY = 2;
                entity.motionZ = entity.getLookVec().z * targetSpeed;
                data.getMiscData().addFallAbsorption(10);

                if (world.isRemote) {
                    world.playSound(entity.posX, entity.posY, entity.posZ,
                            SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 1.0F, false);
                    explosion(entity, dive, world, size * 0.75F, rings, particles, -2);
                    explosion(entity, dive, world, size * 0.75F, rings, particles, -world.rand.nextFloat() * 2);
                }
                //Explode
                if (!world.isRemote) {
                    AxisAlignedBB targetBox = entity.getEntityBoundingBox().grow(dive.getProperty(Ability.SIZE,
                            abilityData).floatValue() / 2);
                    List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                            targetBox);
                    if (!targets.isEmpty()) {
                        for (EntityLivingBase hit : targets) {
                            if (DamageUtils.canDamage(entity, hit)) {
                                hit.attackEntityFrom(AvatarDamageSource.COMBUSTION,
                                        dive.getProperty(Ability.DAMAGE, abilityData).floatValue() / 2);
                                Vec3d vel = hit.getPositionVector().subtract(entity.getPositionVector()).scale(
                                        dive.getProperty(Ability.KNOCKBACK, abilityData).floatValue() / 2);
                                hit.addVelocity(vel.x, vel.y, vel.z);
                            }
                        }
                    }
                }
            } else if (entity.onGround && duration > lifetime + 5 && abilityData.getUseNumber() < 2) {
                if (world.isRemote) {
                    world.playSound(entity.posX, entity.posY, entity.posZ,
                            SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
                    explosion(entity, dive, world, size * 1.5F, rings, particles, -2);
                    explosion(entity, dive, world, size * 4F, rings, particles, -6);
                }
                int radius = dive.getProperty(Ability.SIZE, abilityData).intValue();
                for (int x = -(radius / 2); x < radius / 2; x++) {
                    for (int z = -(radius / 2); z < radius / 2; z++) {
                        BlockPos pos = entity.getPosition();
                        pos = pos.add(x, 0, z);
                        int blockTime = 20 + 20 * Math.max(Math.abs(x), Math.abs(z));

                        if (Blocks.FIRE.canPlaceBlockAt(world, pos) && !world.getBlockState(pos).isFullBlock())
                            BlockTemp.createTempBlock(world, pos, blockTime, Blocks.FIRE.getDefaultState());

                    }
                    AxisAlignedBB targetBox = entity.getEntityBoundingBox().grow(dive.getProperty(Ability.SIZE,
                            abilityData).floatValue());
                    List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                            targetBox);
                    if (!targets.isEmpty()) {
                        for (EntityLivingBase hit : targets) {
                            if (DamageUtils.canDamage(entity, hit)) {
                                hit.attackEntityFrom(AvatarDamageSource.COMBUSTION,
                                        dive.getProperty(Ability.DAMAGE, abilityData).floatValue());
                                Vec3d vel = hit.getPositionVector().subtract(entity.getPositionVector()).scale(
                                        dive.getProperty(Ability.KNOCKBACK, abilityData).floatValue());
                                hit.addVelocity(vel.x, vel.y, vel.z);
                            }
                        }
                    }
                }
                abilityData.setUseNumber(abilityData.getUseNumber() + 1);
            }
        }
        return abilityData.getUseNumber() > 1;

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        ctx.getData().getAbilityData("megaton_dive").setUseNumber(0);

    }

    private void explosion(EntityLivingBase entity, AbilityMegatonDive dive, World world, float size, int rings, int particles, float velMult) {
        ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(BendingStyles.get(Firebending.ID)).ability(dive).spawnEntity(entity)
                .clr(255, 20 + AvatarUtils.getRandomNumberInRange(0, 60), 10, AvatarUtils.getRandomNumberInRange(60, 80)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 80)
                .collideParticles(AvatarUtils.getRandomNumberInRange(1, 100) > 700)
                .scale(size * AvatarUtils.getRandomNumberInRange(1, 3) / 2).time(24 + AvatarUtils.getRandomNumberInRange(1, 2)).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 70)
                .swirl(rings, particles, size * 6, 0.75F, 80, 2 * size * velMult, entity,
                        world, false, AvatarEntityUtils.getMiddleOfEntity(entity), ParticleBuilder.SwirlMotionType.OUT,
                        false, true);
    }
}
