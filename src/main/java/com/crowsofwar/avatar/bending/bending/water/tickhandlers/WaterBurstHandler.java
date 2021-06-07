package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterBlast;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.IOffensiveEntity;
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

import java.util.ArrayList;
import java.util.List;

import static com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.BURST_WATER;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_WATER;
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


        int duration = ctx.getData().getTickHandlerDuration(this);


        Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
                Math.toRadians(entity.rotationPitch));
        Vector pos = Vector.getEyePos(entity);

        if (burst != null && abilityData != null && bender.consumeChi(burst.getChiCost(abilityData))) {

            //Only used for determining the charge amount
            int durationToFire = burst.getProperty(AbilityWaterBlast.PULL_TIME, abilityData).intValue();

            durationToFire *= (2 - abilityData.getDamageMult());
            durationToFire -= abilityData.getXpModifier() * 10;


            float damage = burst.getProperty(Ability.DAMAGE, abilityData).floatValue();
            float speed = burst.getProperty(Ability.SPEED, abilityData).floatValue() * 5;
            float size = burst.getProperty(Ability.BURST_RADIUS, abilityData).floatValue();
            int performance = burst.getProperty(Ability.PERFORMANCE, abilityData).intValue();
            float xp = burst.getProperty(Ability.XP_HIT, abilityData).intValue();
            float length = burst.getProperty(Ability.BURST_RANGE, abilityData).floatValue();
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
            charge = Math.max((3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4

            size = burst.powerModify(size, abilityData);
            damage = burst.powerModify(damage, abilityData);
            speed = burst.powerModify(size, abilityData);
            length = burst.powerModify(length, abilityData);

            damage *= (0.5 + 0.125 * charge);
            size *= (0.5 + 0.125 * charge);
            speed *= (0.5 + 0.125 * charge);

            //Two methods for cones: create randomised particle shape, or search it up on ProjectKorra.
            //You know what I'm gonna do.

            if (world.isRemote) {
                double dtheta = 360.0 / (2 * Math.PI * length) - 1;
                for (double theta = 0; theta < 360; theta += dtheta) {
                    double rtheta = Math.toRadians(theta);
                    Vector vector = new Vector(Math.cos(rtheta), 0, Math.sin(rtheta));
                    if (vector.angle(Vector.getLookRectangular(entity)) < burst.getProperty
                            (Ability.CONE_WIDTH, abilityData).intValue()) {
                        //Water cube time
                        //What if swirl???
                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).element(new Waterbending())
                                .clr(0, 102, 255, 145).pos(vector.toMinecraft())
                                .vel(vector.normalize().toMinecraft().add(world.rand.nextGaussian() / 10,
                                        world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10))
                                .spawnEntity(entity).scale(size / 5).ability(burst)
                                .collideParticles(true).spawn(world);
                    }
                }
            }
            //Time to damage stuff
            if (!world.isRemote) {
                List<Entity> hit = new ArrayList<>();
                //What we wanna do is kind of approximate the hitbox, so we just make a bunch of AABB things
                //and make them the same size as the particle.
                double dtheta = 360.0 / (2 * Math.PI * length) - 1;
                for (double theta = 0; theta < 360; theta += dtheta) {
                    double rtheta = Math.toRadians(theta);
                    Vector vector = new Vector(Math.cos(rtheta), 0, Math.sin(rtheta));
                    if (vector.angle(Vector.getLookRectangular(entity)) < burst.getProperty
                            (Ability.CONE_WIDTH, abilityData).intValue()) {
                        AxisAlignedBB hitBox = new AxisAlignedBB(vector.x() - size / 10,
                                vector.y() - size / 10, vector.z() - size / 10, vector.x() + size / 10,
                                vector.y() + size / 10, vector.z() + size / 10);
                        hit.addAll(world.getEntitiesWithinAABB(Entity.class, hitBox));
                    }
                }

                //Then we wanna check through the player's vision to make sure they didn't miss it
                // (e.g it's sitting in the middle of the cone)
                hit.addAll(Raytrace.entityRaytrace(world, Vector.getEyePos(entity).minusY(0.15).toMinecraft(),
                        entity.getLookVec(), size / 2, length));

                if (!hit.isEmpty()) {
                    for (Entity target : hit) {
                        if (DamageUtils.isDamageable(entity, target)) {
                            DamageUtils.attackEntity(entity, target,
                                    AvatarDamageSource.causeWaterCannonDamage(target, entity), damage, performance,
                                    burst, xp);
                            Vec3d vel = entity.getLookVec().scale(speed);
                            target.addVelocity(vel.x, vel.y, vel.z);
                            AvatarUtils.afterVelocityAdded(target);
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

            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, entity.getSoundCategory(),
                    1.0F + Math.max(abilityData.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_LIGHTNING_IMPACT, entity.getSoundCategory(), 2.0F, 3.0F);

            AttributeModifier modifier = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
            if (modifier != null && entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(modifier)) {
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
            }
        }
        ctx.getData().removeTickHandler(WATER_CHARGE, ctx);
        ctx.getData().removeStatusControl(CHARGE_WATER);
        ctx.getData().removeStatusControl(BURST_WATER);
        return true;
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
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "air_burst");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
    }
}
