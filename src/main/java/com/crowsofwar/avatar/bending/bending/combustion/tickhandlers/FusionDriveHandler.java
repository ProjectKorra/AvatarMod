package com.crowsofwar.avatar.bending.bending.combustion.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityFusionDrive;
import com.crowsofwar.avatar.bending.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class FusionDriveHandler extends TickHandler {

    public FusionDriveHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        int duration = data.getTickHandlerDuration(this);
        AbilityFusionDrive drive = (AbilityFusionDrive) Abilities.get("fusion_drive");
        AbilityData abilityData = AbilityData.get(entity, "fusion_drive");

        Vec3d lookPos = entity.getLookVec().scale(1.5).add(entity.getPositionVector().add(0, entity.getEyeHeight() * 0.75, 0));
        if (drive != null && abilityData != null) {
            float size = drive.getProperty(Ability.SIZE, abilityData).floatValue();
            float damage = drive.getProperty(Ability.DAMAGE, abilityData).floatValue();
            float speed = drive.getProperty(Ability.SPEED, abilityData).floatValue() / 4;
            float knockback = drive.getProperty(Ability.KNOCKBACK, abilityData).floatValue() / 4;
            int fireTime = drive.getProperty(Ability.FIRE_TIME, abilityData).intValue();

            int rings = (int) (size * 6);
            //The size is the radius, circumference uses the diameter
            int particles = (int) (Math.PI * 2 * size) * 2;
            if (duration == 1) {
                if (world.isRemote) {
                    swingArm(entity, false);
                    //Implosion
                    //Gold
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, AvatarUtils.getRandomNumberInRange(220, 255),
                                    AvatarUtils.getRandomNumberInRange(0, 60), AvatarUtils.getRandomNumberInRange(5, 20))
                            .glow(true).spawnEntity(entity).element(BendingStyles.get(Combustionbending.ID))
                            .time(20 + AvatarUtils.getRandomNumberInRange(0, 4)).swirl(rings, particles, size / 2,
                                    particles / 100F, size * 60, 1.125F, entity, world, false, lookPos, ParticleBuilder.SwirlMotionType.IN,
                                    false, true);
                    //Red
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, AvatarUtils.getRandomNumberInRange(0, 60),
                                    AvatarUtils.getRandomNumberInRange(0, 60), AvatarUtils.getRandomNumberInRange(10, 15))
                            .glow(AvatarUtils.getRandomNumberInRange(1, 100) > 70).spawnEntity(entity).element(BendingStyles.get(Combustionbending.ID))
                            .time(20 + AvatarUtils.getRandomNumberInRange(0, 4)).swirl(rings, particles / 4, size / 2, particles / 100F,
                                    size * 60, 1.125F, entity, world, false, lookPos, ParticleBuilder.SwirlMotionType.IN,
                                    false, true);
                }
                if (!world.isRemote)
                    pullEntities(world, entity, speed / 5, size * 2);
            }
            Vec3d look = entity.getLookVec();
            float eyePos = (float) entity.getPositionVector().add(0, entity.getEyeHeight() * 0.675, 0).y;

            if (duration == 10) {
                if (world.isRemote) {
                    swingArm(entity, true);
                    //Explosion
                    //Spawn particles
                    for (int i = 0; i < 48 + particles * 4; i++) {
                        float accuracyMult = AvatarUtils.getRandomNumberInRange(1, 2) * 0.0675F;
                        float mult = speed * AvatarUtils.getRandomNumberInRange(1, 3);
                        double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionX;
                        double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
                        double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionZ;


                        //Gold
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                                .element(BendingStyles.get(Combustionbending.ID)).ability(drive).spawnEntity(entity)
                                .clr(255, AvatarUtils.getRandomNumberInRange(200, 250), AvatarUtils.getRandomNumberInRange(0, 50), AvatarUtils.getRandomNumberInRange(60, 100)).collide(world.rand.nextBoolean())
                                .scale(size * AvatarUtils.getRandomNumberInRange(1, 3) / 2).time(18 + AvatarUtils.getRandomNumberInRange(1, 5)).glow(true).
                                spawn(world);
                        //Red
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                                .element(BendingStyles.get(Firebending.ID)).ability(drive).spawnEntity(entity)
                                .clr(255, 20 + AvatarUtils.getRandomNumberInRange(0, 60), 10, AvatarUtils.getRandomNumberInRange(60, 80)).collide(true).collideParticles(true)
                                .scale(size * AvatarUtils.getRandomNumberInRange(1, 3) / 2).time(18 + AvatarUtils.getRandomNumberInRange(1, 5)).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 70)
                                .spawn(world);
                    }
                }
                if (!world.isRemote) {
                    AxisAlignedBB box = new AxisAlignedBB(lookPos.x + size, lookPos.y + size,
                            lookPos.z + size, lookPos.x - size, lookPos.y - size, lookPos.z - size);
                    List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
                    if (!entities.isEmpty()) {
                        for (Entity target : entities) {
                            if (DamageUtils.canDamage(entity, target)) {
                                DamageUtils.attackEntity(entity, target, AvatarDamageSource.COMBUSTION,
                                        damage, 20, drive, 1);
                                Vec3d vel = look.scale(knockback * 4);
                                target.motionX = vel.x;
                                target.motionY = vel.y + 0.15;
                                target.motionZ = vel.z;
                                target.isAirBorne = true;
                            }
                        }
                    }
                    //Knockback
                }
                world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE,
                        SoundCategory.PLAYERS, 2.0F, 0.5F + world.rand.nextFloat() / 2F, true);

            }
            if (duration > 10 && duration % 2 == 0) {
                if (world.isRemote)
                    for (int i = 0; i < 48 + particles * 4; i++) {
                        float accuracyMult = AvatarUtils.getRandomNumberInRange(1, 2) * 0.0675F;
                        float mult = speed * AvatarUtils.getRandomNumberInRange(1, 3);
                        double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionX;
                        double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
                        double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionZ;


                        //Gold
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                                .element(BendingStyles.get(Combustionbending.ID)).ability(drive).spawnEntity(entity)
                                .clr(255, AvatarUtils.getRandomNumberInRange(200, 250), AvatarUtils.getRandomNumberInRange(0, 50), AvatarUtils.getRandomNumberInRange(60, 100)).collide(world.rand.nextBoolean())
                                .scale(size * AvatarUtils.getRandomNumberInRange(1, 3) / 2).time(18 + AvatarUtils.getRandomNumberInRange(1, 5)).glow(true).
                                spawn(world);
                        //Red
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                                .element(BendingStyles.get(Firebending.ID)).ability(drive).spawnEntity(entity)
                                .clr(255, 20 + AvatarUtils.getRandomNumberInRange(0, 60), 10, AvatarUtils.getRandomNumberInRange(60, 80)).collide(true).collideParticles(true)
                                .scale(size * AvatarUtils.getRandomNumberInRange(1, 3) / 2).time(18 + AvatarUtils.getRandomNumberInRange(1, 5)).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 70)
                                .spawn(world);
                    }
            }
        }
        return duration > 15;
    }

    private void swingArm(EntityLivingBase entity, boolean main) {
        if (main) entity.swingArm(EnumHand.MAIN_HAND);
        else entity.swingArm(EnumHand.OFF_HAND);
    }

    private void pullEntities(World world, EntityLivingBase entity, float pullForce, float range) {
        Vec3d look = entity.getLookVec();
        Vec3d pos = look.add(entity.getPositionVector().add(0, entity.getEyeHeight() * 0.75, 0));
        AxisAlignedBB box = new AxisAlignedBB(pos.x + range, pos.y + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
        if (!entities.isEmpty()) {
            for (Entity target : entities) {
                if (DamageUtils.canCollideWith(entity, target) && target != entity) {
                    Vec3d vel = pos.subtract(target.getPositionVector()).scale(pullForce);
                    target.motionX = vel.x;
                    target.motionY = vel.y + 0.15;
                    target.motionZ = vel.z;
                    target.isAirBorne = true;
                }
            }
        }
    }
}
