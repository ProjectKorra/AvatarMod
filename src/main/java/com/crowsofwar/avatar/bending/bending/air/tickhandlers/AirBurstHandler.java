package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.*;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_AIR_BURST;
import static com.crowsofwar.avatar.util.data.StatusControlController.SHOOT_AIR_BURST;

public class AirBurstHandler extends TickHandler {
    public static final UUID AIRBURST_MOVEMENT_MODIFIER_ID = UUID.fromString
            ("f82d325c-9828-11e8-9eb6-529269fb1459");

    public AirBurstHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("air_burst");
        float charge;
        //4 stages, max charge of 4.
        boolean shouldRemove = false;

        //TODO: Air Blast/Laser of Air! At level 1, it activates at charge level 4.
        if (abilityData != null) {

            float powerRating = ((float) bender.getDamageMult(Airbending.ID));
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            double damage = STATS_CONFIG.airBurstSettings.damage;
            //Default 5
            float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40F);
            double knockBack = STATS_CONFIG.airBurstSettings.push;
            //Default 2 + Power rating
            float radius = STATS_CONFIG.airBurstSettings.radius;
            //Default 4
            float durationToFire = STATS_CONFIG.airBurstSettings.durationToFire;
            //Default 40
            double upwardKnockback = STATS_CONFIG.airBurstSettings.push / 40;
            double suction = 0.05;
            int performanceAmount = STATS_CONFIG.airBurstSettings.performanceAmount;
            float shockwaveSpeed;

            //Makes sure the charge is never 0.
            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.


            if (abilityData.getLevel() == 1) {
                damage *= 1.5;
                //7.5
                knockBack *= 1.25;
                radius *= 1.25;
                //4
                durationToFire *= 0.825F;
                //30
                upwardKnockback *= 1.5;
                performanceAmount += 3;
            }

            if (abilityData.getLevel() >= 2) {
                damage *= 2;
                //8
                knockBack *= 2;
                radius *= 1.75F;
                //7
                durationToFire *= 0.75F;
                //20
                upwardKnockback *= 1.75;
                performanceAmount += 5;
            }

            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                //Piercing Winds
                damage *= 1.25;
                //Blinds enemies

            }

            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                //Maximum Pressure
                //Pulls enemies in then blasts them out
                radius *= 1.5;
                //10.5
                upwardKnockback = STATS_CONFIG.airBurstSettings.push / 10;
                durationToFire *= (4 / 3F);
                //Back to the original amount.
                performanceAmount += 2;
            }

            durationToFire *= (1 / powerRating);
            durationToFire -= xpMod * 10;
            damage *= powerRating * xpMod;
            radius *= powerRating * xpMod;
            knockBack *= powerRating * xpMod;


            //how fast the shockwave's particle speed is.
            shockwaveSpeed = (float) knockBack;
            //Affect things by the charge. The charge, at stage 3, should set everything to its max.
            damage *= (0.20 + 0.20 * charge);
            //Results in a bigger radius so that it blocks projectiles.
            radius *= (0.60 + 0.10 * charge);
            knockBack *= (0.60 + 0.10 * charge);
            performanceAmount *= (0.20 + 0.20 * charge);


            applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
            double inverseRadius = (durationToFire - duration) / 10;
            //gets smaller
            suction -= (float) duration / 400;

            if (world.isRemote && duration <= durationToFire) {
                for (int i = 0; i < 12; i++) {
                    Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
                            i * 30), 0).times(inverseRadius).withY(entity.getEyeHeight() / 2);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(lookpos.toMinecraft()))
                            .collide(true).scale(abilityData.getXpModifier() * 0.85F * charge).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                            world.rand.nextGaussian() / 60).clr(0.975F, 0.975F, 0.975F, 0.1F).element(new Airbending()).spawn(world);
                }
            }
            world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.8F + world.rand.nextFloat() / 10);

            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + radius, entity.posZ + radius, entity.posX - radius, entity.posY - radius, entity.posZ - radius);
                List<Entity> collided = world.getEntitiesWithinAABB(Entity.class, box, entity1 -> entity1 != entity);
                if (!collided.isEmpty()) {
                    for (Entity e : collided) {
                        if (e.canBePushed() && e.canBeCollidedWith() && e != entity) {
                            pullEntities(e, entity, suction);
                        }
                    }
                }
            }

            //Applies the proper status control based on level.
            switch (abilityData.getLevel()) {
                case -1:
                case 0:
                    if (charge == 4) {
                        addStatCtrl(data);
                        shouldRemove = true;
                    }
                    break;

                case 1:
                    if (charge >= 3) {
                        addStatCtrl(data);
                        shouldRemove = true;
                    }
                    break;
                case 2:
                    if (charge >= 2) {
                        addStatCtrl(data);
                        shouldRemove = true;
                    }
                    break;
                case 3:
                    if (charge > 0 && abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                        addStatCtrl(data);
                        shouldRemove = true;
                    } else if (charge > 1) {
                        addStatCtrl(data);
                        shouldRemove = true;
                    }
                    break;
            }


            if (!data.hasStatusControl(RELEASE_AIR_BURST)) {

                int particleController = abilityData.getLevel() > 0 ? 50 - (4 * abilityData.getLevel()) : 50;
                EntityShockwave shockwave = new EntityShockwave(world);
                shockwave.setOwner(entity);
                shockwave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity));
                shockwave.setRenderNormal(false);
                shockwave.setElement(new Airbending());
                shockwave.setParticleSpeed(0.5F * radius / STATS_CONFIG.airBurstSettings.radius);
                shockwave.setDamageSource(AvatarDamageSource.AIR);
                shockwave.setKnockbackHeight(upwardKnockback);
                shockwave.setKnockbackMult(new Vec3d(knockBack, knockBack / 2, knockBack));
                shockwave.setDamage((float) damage);
                shockwave.setParticleAmount(1);
                shockwave.setRange(radius);
                shockwave.setSphere(true);
                shockwave.setPerformanceAmount(performanceAmount);
                shockwave.setParticleSpeed(Math.min((float) knockBack / shockwaveSpeed * 1.5F, shockwaveSpeed));
                shockwave.setParticleController(particleController);
                shockwave.setAbility(new AbilityAirBurst());
                shockwave.setSpeed((float) knockBack / 4);
                shockwave.setBehaviour(new AirburstShockwave());
                world.spawnEntity(shockwave);


                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AIRBURST_MOVEMENT_MODIFIER_ID);

                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                        SoundCategory.BLOCKS, 1, 0.5F);

                data.removeStatusControl(SHOOT_AIR_BURST);
                return true;
            }
            return !data.hasStatusControl(RELEASE_AIR_BURST) || shouldRemove && !data.hasStatusControl(SHOOT_AIR_BURST);
        } else {
            data.removeStatusControl(SHOOT_AIR_BURST);
            return true;
        }
    }

    private void addStatCtrl(BendingData data) {
        if (!data.hasStatusControl(SHOOT_AIR_BURST)) {
            data.addStatusControl(SHOOT_AIR_BURST);
        }
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(AIRBURST_MOVEMENT_MODIFIER_ID);

        moveSpeed.applyModifier(new AttributeModifier(AIRBURST_MOVEMENT_MODIFIER_ID,
                "Airburst charge modifier", multiplier - 1, 1));

    }

    private void pullEntities(Entity collided, Entity attacker, double suction) {
        Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
        velocity = velocity.times(suction).times(-1);

        double x = (velocity.x());
        double y = (velocity.y());
        double z = (velocity.z());

        if (!collided.world.isRemote) {
            collided.addVelocity(x, y, z);

            if (collided instanceof AvatarEntity) {
                if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
                    AvatarEntity avent = (AvatarEntity) collided;
                    avent.addVelocity(x, y, z);
                }
                collided.isAirBorne = true;
                AvatarUtils.afterVelocityAdded(collided);
            }
        }
    }

    public static class AirburstShockwave extends OffensiveBehaviour {

        @Override
        public Behavior onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityShockwave) {
                World world = entity.world;
                if (world.isRemote) {
                    //TODO: Fix particle speed
                    if (entity.ticksExisted == 2) {
                        double x1, y1, z1, xVel, yVel, zVel;
                        if (CLIENT_CONFIG.airRenderSettings.airBurstSphere) {
                            for (double theta = 0; theta <= 180; theta += 1) {
                                double dphi = (((EntityShockwave) entity).getParticleController() - ((EntityShockwave) entity).getParticleAmount()) / Math.sin(Math.toRadians(theta));
                                for (double phi = 0; phi < 360; phi += dphi) {
                                    double rphi = Math.toRadians(phi);
                                    double rtheta = Math.toRadians(theta);

                                    x1 = entity.ticksExisted * ((EntityShockwave) entity).getSpeed() * Math.cos(rphi) * Math.sin(rtheta);
                                    y1 = entity.ticksExisted * ((EntityShockwave) entity).getSpeed() * Math.sin(rphi) * Math.sin(rtheta);
                                    z1 = entity.ticksExisted * ((EntityShockwave) entity).getSpeed() * Math.cos(rtheta);
                                    xVel = x1 * entity.getParticleSpeed() * 0.375F;
                                    yVel = y1 * entity.getParticleSpeed() * 0.375F;
                                    zVel = z1 * entity.getParticleSpeed() * 0.375F;

                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1 + entity.posX, y1 + entity.posY, z1 + entity.posZ).vel(xVel, yVel, zVel)
                                            .clr(0.95F, 0.95F, 0.95F, 0.075F).time(12 + AvatarUtils.getRandomNumberInRange(0, 10) + (int) (3 * ((EntityShockwave) entity).getRange() / STATS_CONFIG.airBurstSettings.radius)).collide(true)
                                            .scale(0.325F + 0.5F * (float) ((EntityShockwave) entity).getRange() / STATS_CONFIG.airBurstSettings.radius).element(entity.getElement())
                                            .element(new Airbending()).spawn(world);

                                }
                            }

                        } //else {
                        for (double i = 0; i < ((EntityShockwave) entity).getRange() + ((EntityShockwave) entity).getParticleAmount(); i += 0.02) {
                            Vec3d vel = new Vec3d(world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
                            vel = vel.scale(0.275F * entity.getParticleSpeed());
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(entity.posX, entity.posY, entity.posZ).vel(vel)
                                    .clr(0.95F, 0.95F, 0.95F, 0.075F).time(12 + AvatarUtils.getRandomNumberInRange(0, 10)).collide(true)
                                    .scale(0.4f + 0.575F * (float) ((EntityShockwave) entity).getRange() / STATS_CONFIG.airBurstSettings.radius).
                                    element(new Airbending()).spawn(world);

                        }
                    }
                    //}
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
