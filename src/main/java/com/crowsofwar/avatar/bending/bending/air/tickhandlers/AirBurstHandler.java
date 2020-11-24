package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.*;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst.*;
import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;
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
        AbilityAirBurst burst = (AbilityAirBurst) Abilities.get("air_burst");

        float charge;
        //4 stages, max charge of 4.
        boolean shouldRemove = false;

        //TODO: Air Blast/Laser of Air! At level 1, it activates at charge level 4.
        if (abilityData != null && burst != null) {

            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = burst.getProperty(EFFECT_DAMAGE, abilityData).floatValue();
            float slowMult = burst.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = burst.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = burst.getProperty(EFFECT_RADIUS, abilityData).floatValue();
            float durationToFire = burst.getProperty(CHARGE_TIME, abilityData).intValue();
            double suction = 0.05;
            float speed = burst.getProperty(SPEED, abilityData).floatValue() / 5;
            int performanceAmount = burst.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = burst.getExhaustion(abilityData);
            burnout = burst.getBurnOut(abilityData);
            cooldown = burst.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            durationToFire *= (2 - powerMod);
            durationToFire -= xpMod * 10;
            damage *= powerMod * xpMod;
            radius *= powerMod * xpMod;
            knockBack *= powerMod * xpMod;
            slowMult *= powerMod * xpMod;
            speed *= powerMod * xpMod;

            float movementMultiplier = slowMult - 0.7f * MathHelper.sqrt(duration / 40F);

            //how fast the shockwave's particle speed is.
            shockwaveSpeed = knockBack;
            //Affect things by the charge. The charge, at stage 3, should set everything to its max.
            damage *= (0.20 + 0.20 * charge);
            //Results in a bigger radius so that it blocks projectiles.
            radius *= (0.60 + 0.10 * charge);
            knockBack *= (0.60 + 0.10 * charge);
            speed *= (1F + charge / 4F);
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
                            world.rand.nextGaussian() / 60).clr(0.975F, 0.975F, 0.975F, 0.05F).element(new Airbending()).spawn(world);
                }
            }
            world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.8F + world.rand.nextFloat() / 10);

            if (burst.getBooleanProperty(PULL_ENEMIES, abilityData)) {
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
            if (charge >= burst.getProperty(BLAST_LEVEL, abilityData).intValue()) {
                addStatCtrl(data);
                shouldRemove = true;
            }


            if (!data.hasStatusControl(RELEASE_AIR_BURST) && bender.consumeChi(burst.getChiCost(abilityData))) {

                int particleController = abilityData.getLevel() > 0 ? 48 - (6 * Math.max(abilityData.getLevel(), 0)) : 48;
                EntityShockwave shockwave = new EntityShockwave(world);
                shockwave.setOwner(entity);
                shockwave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity));
                shockwave.setRenderNormal(false);
                shockwave.setElement(new Airbending());
                shockwave.setParticleSpeed(speed);
                shockwave.setDamageSource("avatar_Air_sphere_shockwave");
                shockwave.setKnockbackMult(new Vec3d(knockBack, knockBack / 2, knockBack));
                shockwave.setDamage(damage);
                shockwave.setParticleAmount(1);
                shockwave.setRange(radius);
                shockwave.setSphere(true);
                shockwave.setTier(burst.getCurrentTier(abilityData));
                shockwave.setXp(burst.getProperty(XP_HIT, abilityData).floatValue());
                shockwave.setPerformanceAmount(performanceAmount);
                shockwave.setParticleSpeed(Math.min(knockBack / shockwaveSpeed * 1.5F, shockwaveSpeed));
                shockwave.setParticleController(particleController);
                shockwave.setAbility(Objects.requireNonNull(Abilities.get("air_burst")));
                shockwave.setSpeed(speed / 2);
                shockwave.setBehaviour(new AirburstShockwave());
                if (!world.isRemote)
                    world.spawnEntity(shockwave);
                abilityData.addBurnout(burnout);
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);


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

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "air_burst");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AIRBURST_MOVEMENT_MODIFIER_ID);

    }

    public static class AirburstShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
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
                                    xVel = x1 * entity.getParticleSpeed() * 0.375 + world.rand.nextGaussian() / 16;
                                    yVel = y1 * entity.getParticleSpeed() * 0.375 + world.rand.nextGaussian() / 16;
                                    zVel = z1 * entity.getParticleSpeed() * 0.375 + world.rand.nextGaussian() / 16;

                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1 + entity.posX, y1 + entity.posY, z1 + entity.posZ).vel(xVel, yVel, zVel)
                                            .clr(0.95F, 0.95F, 0.95F, 0.05F).time(12 + AvatarUtils.getRandomNumberInRange(2, 4)).collide(true).collideParticles(true)
                                            .scale((float) (0.325F + 0.5F * ((EntityShockwave) entity).getSpeed() * (float) ((EntityShockwave) entity).getRange()))
                                            .element(new Airbending()).spawnEntity(entity.getOwner()).spawn(world);

                                }
                            }

                        } //else {
                        for (double i = 0; i < ((EntityShockwave) entity).getRange() + ((EntityShockwave) entity).getParticleAmount(); i += 0.5) {
                            Vec3d vel = new Vec3d(world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
                            vel = vel.scale(0.3F * entity.getParticleSpeed());
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(entity.posX, entity.posY, entity.posZ).vel(vel)
                                    .clr(0.95F, 0.95F, 0.95F, 0.075F).time(8 + AvatarUtils.getRandomNumberInRange(0, 10)).collide(true).collideParticles(true)
                                    .scale((float) (0.4f + 0.575F * (float) ((EntityShockwave) entity).getRange() * ((EntityShockwave) entity).getSpeed())).
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
