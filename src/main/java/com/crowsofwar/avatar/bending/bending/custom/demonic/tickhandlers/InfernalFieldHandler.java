package com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityInfernalField;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityInfernalBall;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.AvatarMod.shoulderSurfingCompat;
import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst.SLOW_MULT;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_INFERNAL_FIELD;

public class InfernalFieldHandler extends TickHandler {

    public static final UUID INFERNAL_FIELD_MOVEMENT_MOD_ID = UUID.randomUUID();

    public InfernalFieldHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("infernal_field");
        AbilityInfernalField infernalField = (AbilityInfernalField) Abilities.get("infernal_field");

        float charge;
        //4 stages, max charge of 4.


        if (abilityData != null && infernalField != null) {

            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = infernalField.getProperty(DAMAGE, abilityData).floatValue();
            float slowMult = infernalField.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = infernalField.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = infernalField.getProperty(SIZE, abilityData).floatValue();
            float durationToFire = infernalField.getProperty(CHARGE_TIME, abilityData).intValue();
            double suction = 0.05;
            int lifetime = infernalField.getProperty(LIFETIME, abilityData).intValue();
            float speed = infernalField.getProperty(SPEED, abilityData).floatValue() / 3;
            int performanceAmount = infernalField.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = infernalField.getExhaustion(abilityData);
            burnout = infernalField.getBurnOut(abilityData);
            cooldown = infernalField.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            durationToFire *= (2 - powerMod);
            durationToFire -= xpMod * 10;
            damage = infernalField.powerModify(damage, abilityData) * 3;
            radius = infernalField.powerModify(radius, abilityData);
            knockBack = infernalField.powerModify(knockBack, abilityData);
            slowMult = infernalField.powerModify(slowMult, abilityData);
            speed = infernalField.powerModify(speed, abilityData);

            float movementMultiplier = slowMult - 0.7f * MathHelper.sqrt(duration / 40F);

            //how fast the shockwave's particle speed is.
            shockwaveSpeed = knockBack;
            //Affect things by the charge. The charge, at stage 3, should set everything to its max.
            damage *= (0.50 + 0.20 * charge);
            //Results in a bigger radius so that it blocks projectiles.
            radius *= (0.60 + 0.10 * charge);
            knockBack *= (0.60 + 0.10 * charge);
            speed *= (1F + charge / 4F);
            performanceAmount *= (0.20 + 0.20 * charge);
            lifetime *= (0.75 + 0.125 * charge);


            applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
            double inverseRadius = (durationToFire - duration) / 10;
            //gets smaller

            //Show lance charging here
            Vec3d height, rightSide;

            double distMult = 1;
            //I need two spinning vortexes, facing away from the centre of the entity
            //Located at the player's main hand (in first person)
            //Copied from flame strike:
            if (entity instanceof EntityPlayer) {
                if (!AvatarMod.realFirstPersonRender2Compat && !shoulderSurfingCompat
                        && (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1)) {
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
                    distMult = 1.25;
                    height = entity.getPositionVector().add(0, 0.84, 0);
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                    } else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                    }
                }
            } else {
                distMult = 2;
                height = entity.getPositionVector().add(0, 0.84, 0);
                if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                } else {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                }

            }
            rightSide = rightSide.add(height);

            if (world.isRemote) {
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(true).element(BendingStyles.get(Darkbending.ID))
                        .clr(120, 40, 40).scale(0.25F).time(32).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                        radius, (float) (0.25 / radius),
                        60, 1 / radius, entity, world, false, rightSide, ParticleBuilder.SwirlMotionType.IN, false,
                        true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 40)
                        .clr(20, 20, 20).scale(0.25F).time(8).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                        radius, (float) (0.25 / radius),
                        60, 1 / radius, entity, world, false, rightSide, ParticleBuilder.SwirlMotionType.IN, false,
                        true);

            }
            world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 0.25F * charge, 0.2F + world.rand.nextFloat() / 10);


            if (!data.hasStatusControl(RELEASE_INFERNAL_FIELD)) {

                EntityInfernalBall infernalBall = new EntityInfernalBall(world);
                infernalBall.setEntitySize(radius);
                infernalBall.setDamage(damage);
                infernalBall.setAbility(Objects.requireNonNull(Abilities.get("infernal_field")));
                infernalBall.setDestroyGrass(true);
                infernalBall.setPosition(rightSide.add(entity.getLookVec().scale(-2)));
                infernalBall.setDestroyProjectiles(true);
                infernalBall.setVelocity(entity.getLookVec().scale(speed));
                infernalBall.setSlowProjectiles(false);
                infernalBall.setBehaviour(new InfernalFieldBehaviour());
                infernalBall.setOwner(entity);
                infernalBall.setTier(infernalField.getCurrentTier(abilityData));
                infernalBall.setXp(infernalField.getProperty(XP_HIT, abilityData).floatValue());
                infernalBall.setLifeTime(lifetime);
                infernalBall.setPerformanceAmount(performanceAmount);
                infernalBall.setPiercing(true);
                infernalBall.rotationYaw = entity.rotationYaw;
                infernalBall.rotationPitch = entity.rotationPitch;
                //entity stuff
                //spawn the entity
                if (!world.isRemote)
                    world.spawnEntity(infernalBall);


                abilityData.addBurnout(burnout);
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);


                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID);

                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.BLOCKS, 1, 0.5F);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }


    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID,
                "Infernal Field charge modifier", multiplier - 1, 1));

    }


    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "shade_burst");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID);

    }

    public static class InfernalFieldBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityInfernalBall && entity.getOwner() != null) {
                World world = entity.world;
                //Gravity is 9.82 m/s^2
                entity.addVelocity(new Vec3d(0, -9.82 / 100, 0));
                if (world.isRemote && entity.getOwner() != null) {
                    Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                    float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize());
                    int rings = (int) (entity.getAvgSize() * 8);
                    int particles = (int) (entity.getAvgSize() * 2 * Math.PI);

                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                            .element(BendingStyles.get(entity.getElement())).clr(120, 40, 40).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 30)
                            .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                    entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                            .element(BendingStyles.get(entity.getElement())).clr(10, 10, 10).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                            .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                    entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);


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
