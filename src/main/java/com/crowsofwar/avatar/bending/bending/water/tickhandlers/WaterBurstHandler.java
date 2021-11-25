package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterBlast;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.IOffensiveEntity;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst.PULL_ENEMIES;
import static com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.bending.bending.water.tickhandlers.WaterChargeHandler.WATER_CHARGE_MOVEMENT_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.BURST_WATER;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_WATER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_CHARGE;

public class WaterBurstHandler extends TickHandler {

    public WaterBurstHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityWaterBlast burst = (AbilityWaterBlast) Abilities.get("water_blast");
        AbilityData abilityData = ctx.getData().getAbilityData("water_blast");


        //Doesn't work, because tick handler gets removed in the status control. Should remove it here instead.
        int chargedDuration = ctx.getData().getTickHandlerDuration(WATER_CHARGE);
        int duration = ctx.getData().getTickHandlerDuration(this);


        Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
                Math.toRadians(entity.rotationPitch));
        Vector pos = Vector.getEyePos(entity);

        if (burst != null && abilityData != null && bender.consumeChi(burst.getChiCost(abilityData))) {
            //Maybe use an entity to fix executing twice?

            //Only used for determining the charge amount
            int chargeDuration = burst.getProperty(AbilityWaterBlast.CHARGE_TIME, abilityData).intValue();
            int pullDuration = burst.getProperty(AbilityWaterBlast.PULL_TIME, abilityData).intValue();

            chargeDuration *= (2 - abilityData.getDamageMult());
            chargeDuration -= abilityData.getXpModifier() * 10;


            float damage = burst.getProperty(Ability.DAMAGE, abilityData).floatValue();
            float speed = burst.getProperty(Ability.SPEED, abilityData).floatValue() * 5;
            float size = burst.getProperty(Ability.BURST_RADIUS, abilityData).floatValue();
            int performance = burst.getProperty(Ability.PERFORMANCE, abilityData).intValue();
            float xp = burst.getProperty(Ability.XP_HIT, abilityData).intValue();
            float length = burst.getProperty(Ability.BURST_RANGE, abilityData).floatValue();
            float suction = burst.getProperty(Ability.SUCTION_STRENGTH, abilityData).floatValue();
            int charge;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = burst.getExhaustion(abilityData);
            burnout = burst.getBurnOut(abilityData);
            cooldown = burst.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }
            //Copies the charge calculations
            //Makes sure the charge is never 0.
            charge = Math.max((3 * (chargedDuration / chargeDuration)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4

            size = burst.powerModify(size, abilityData);
            damage = burst.powerModify(damage, abilityData);
            speed = burst.powerModify(speed, abilityData);
            length = burst.powerModify(length, abilityData);
            suction -= (float) duration / pullDuration * suction;

            damage *= (0.5 + 0.125 * charge);
            size *= (0.5 + 0.125 * charge);
            speed *= (0.5 + 0.125 * charge);

            float particleSize = (float) (Math.sqrt(length) / 2);


            /*
                Pull Method stuff
             */
            //TODO: Fix suction
            Vec3d swirlPos = entity.getPositionVector().add(0, entity.getEyeHeight() * 0.75F, 0)
                    .add(entity.getLookVec().scale(length / 4));
            if (burst.getBooleanProperty(PULL_ENEMIES, abilityData)) {
                AxisAlignedBB box = new AxisAlignedBB(entity.posX + length * 0.75F, entity.posY + length  * 0.75F,
                        entity.posZ + length * 0.75F, entity.posX - length * 0.75F, entity.posY - length * 0.75F,
                        entity.posZ - length * 0.75F);
                List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box);
                if (!collided.isEmpty()) {
                    for (Entity e : collided) {
                        if (DamageUtils.isValidTarget(entity, e)) {
                             AvatarEntityUtils.pullEntities(e, swirlPos, suction);
                        }
                    }
                }

                if (world.isRemote) {
                    //Particle time bois and girls (S W O R L)
                    int rings = (int) (length * 2);
                    int particles = (int) length;
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).spawnEntity(entity).scale(particleSize * 0.625F)
                            .time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).element(BendingStyles.get(Waterbending.ID))
                            .clr(0, 102, 255, 65).swirl(rings, particles, length / 5,
                            size / 4, speed * 20, (-1 / speed), entity, world,
                            false, swirlPos, ParticleBuilder.SwirlMotionType.IN, true, true);
                }
            }



            //Also should probably have a method for this lmao (abstraction is good)
            //Ok so when executing once, it doesn't always execute client and server-side.
            Vec3d startPos = Vector.getEyePos(entity).toMinecraft().add(0, -entity.getEyeHeight() / 2, 0);
            //particles spawn twice in case it doesn't execute client-side
            if (duration == pullDuration) {
                if (world.isRemote) {
                    //Water cube time
                    //What if swirl???
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).element(new Waterbending())
                            .clr(0, 102, 255, 145).spawnEntity(entity).scale(particleSize).ability(burst)
                            .time((int) (12 + AvatarUtils.getRandomNumberInRange(0, 2) + size))
                            .collideParticles(true).vortex(world, entity, entity.getLookVec(), (int) length, 20,
                            length / 2, 0.05, size, startPos.x, startPos.y, startPos.z, new Vec3d(0.5, 0.5, 0.5),
                            0.15F, particleSize * 0.75F);


                }
            }
            if (duration == pullDuration + 1) {
                 if (world.isRemote) {
                    //Water cube time
                    //What if swirl???
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).element(new Waterbending())
                            .clr(0, 102, 255, 145).spawnEntity(entity).scale(size).ability(burst)
                            .time((int) (12 + AvatarUtils.getRandomNumberInRange(0, 2) + size))
                            .collideParticles(true).vortex(world, entity, entity.getLookVec(), (int) length, 20,
                            length / 2, 0.05, size, startPos.x, startPos.y, startPos.z, new Vec3d(0.5, 0.5, 0.5),
                            0.15F, size * 0.75F);


                }
                //Time to damage stuff
                if (!world.isRemote) {
                    List<Entity> hit;
                    //What we wanna do is kind of approximate the hitbox, so we just make a bunch of AABB things
                    //and make them the same size as the particle.
                    hit = Raytrace.directionalVortexCollision(world, entity, entity.getLookVec(),
                            (int) (length), 10, length * 1.25, 0.05, size, startPos.x, startPos.y,
                            startPos.z, size / 2);

                    if (!hit.isEmpty()) {
                        for (Entity target : hit) {
                            if (DamageUtils.isDamageable(entity, target)) {
                                Vec3d vel = entity.getLookVec().scale(speed / 2500);
                                target.addVelocity(vel.x, vel.y, vel.z);
                                AvatarUtils.afterVelocityAdded(target);
                                DamageUtils.attackEntity(entity, target,
                                        AvatarDamageSource.causeWaterCannonDamage(target, entity), damage, performance,
                                        burst, xp);
                            } else if (DamageUtils.isValidTarget(entity, target)) {
                                if (target instanceof AvatarEntity) {
                                    handleContact((AvatarEntity) target, burst, abilityData);
                                }
                            }
                        }
                    }
                }

                abilityData.addBurnout(burnout);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.setAbilityCooldown(cooldown);

                entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_GENERIC_SPLASH, entity.getSoundCategory(),
                        1.0F + Math.max(abilityData.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
                entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_LIGHTNING_IMPACT, entity.getSoundCategory(), 2.0F, 3.0F);

                AttributeModifier modifier = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
                if (modifier != null && entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(modifier)) {
                    entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
                }
                return true;
            }
            return false;

        }
        return true;
    }

    public void handleRemoval(BendingContext ctx) {
        ctx.getData().removeTickHandler(WATER_CHARGE, ctx);
        ctx.getData().removeStatusControl(RELEASE_WATER);
        ctx.getData().removeStatusControl(BURST_WATER);
        AttributeModifier mod = ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                .getModifier(WATER_CHARGE_MOVEMENT_ID);
        if (mod != null) {
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(mod);
        }
    }

    public void handleContact(AvatarEntity hit, AbilityWaterBlast blast,
                              AbilityData abilityData) {
        int tier = blast.getProperty(Ability.TIER, abilityData).intValue();
        hit.onMajorWaterContact();
        if (tier > hit.getTier()) {
            if (hit instanceof IOffensiveEntity) {
                ((IOffensiveEntity) hit).Dissipate(hit);
            }
        }

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "water_blast");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        handleRemoval(ctx);
    }
}
