package com.crowsofwar.avatar.bending.bending.ice.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.ice.AbilityIceLance;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityIceLance;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarParticleUtils;
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
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_ICE_LANCE;

public class IceLanceHandler extends TickHandler {
    public static final UUID ICE_LANCE_MOVEMENT_MOD = UUID.randomUUID();

    public IceLanceHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("ice_lance");
        AbilityIceLance iceLance = (AbilityIceLance) Abilities.get("ice_lance");

        float charge;
        //4 stages, max charge of 4.



        if (abilityData != null && iceLance != null) {

            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = iceLance.getProperty(DAMAGE, abilityData).floatValue();
            float slowMult = iceLance.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = iceLance.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = iceLance.getProperty(SIZE, abilityData).floatValue();
            float durationToFire = iceLance.getProperty(CHARGE_TIME, abilityData).intValue();
            double suction = 0.05;
            float speed = iceLance.getProperty(SPEED, abilityData).floatValue() / 3;
            int performanceAmount = iceLance.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = iceLance.getExhaustion(abilityData);
            burnout = iceLance.getBurnOut(abilityData);
            cooldown = iceLance.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            durationToFire *= (2 - powerMod);
            durationToFire -= xpMod * 10;
            damage = iceLance.powerModify(damage, abilityData) * 3;
            radius = iceLance.powerModify(radius, abilityData);
            knockBack = iceLance.powerModify(knockBack, abilityData);
            slowMult  = iceLance.powerModify(slowMult, abilityData);
            speed = iceLance.powerModify(speed, abilityData);

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
                int maxAngle = (int) (radius * 30);
                Vec3d dir = new Vec3d(entity.getLookVec().x, 0, entity.getLookVec().z);
                Vec3d particleSpeed = Vec3d.ZERO;
                if (!data.hasStatusControl(RELEASE_ICE_LANCE))
                    particleSpeed = entity.getLookVec().scale(speed);
                AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, dir.scale(radius * distMult), maxAngle,
                        radius * 2, 0.01, radius / 4, ParticleBuilder.Type.SNOW,
                        rightSide, particleSpeed, Vec3d.ZERO, false, 190, 235, 255, 180, AvatarUtils.getRandomNumberInRange(1, 100) > 80,
                        6, radius / 2, true, -90);
                AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, dir.scale(radius * distMult), maxAngle,
                        radius * 2, 0.01, radius / 4, ParticleBuilder.Type.FLASH,
                        rightSide, particleSpeed, Vec3d.ZERO, true, 130, 235, 255, 40, AvatarUtils.getRandomNumberInRange(1, 100) > 80,
                        10, radius / 4, true, -90);

            }
            world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.8F + world.rand.nextFloat() / 10);


            if (!data.hasStatusControl(RELEASE_ICE_LANCE)) {

                EntityIceLance lance = new EntityIceLance(world);
                lance.setEntitySize(radius / 2, radius * 2);
                lance.setDamage(damage);
                lance.setAbility(Objects.requireNonNull(Abilities.get("ice_lance")));
                lance.setDestroyGrass(true);
                lance.setPosition(rightSide.add(entity.getLookVec().scale(-2)));
                lance.setDestroyProjectiles(true);
                lance.setVelocity(entity.getLookVec().scale(speed));
                lance.setSlowProjectiles(false);
                lance.setBehaviour(new IceLanceBehaviour());
                lance.setOwner(entity);
                lance.setTier(iceLance.getCurrentTier(abilityData));
                lance.setXp(iceLance.getProperty(XP_HIT, abilityData).floatValue());
                lance.setLifeTime((int) (radius * 30));
                lance.setPerformanceAmount(performanceAmount);
                lance.setPiercing(true);
                lance.rotationYaw = entity.rotationYaw;
                lance.rotationPitch = entity.rotationPitch;
                //entity stuff
                //spawn the entity
                if (!world.isRemote)
                    world.spawnEntity(lance);


                abilityData.addBurnout(burnout);
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);


                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_LANCE_MOVEMENT_MOD);

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

        moveSpeed.removeModifier(ICE_LANCE_MOVEMENT_MOD);

        moveSpeed.applyModifier(new AttributeModifier(ICE_LANCE_MOVEMENT_MOD,
                "Ice Lance charge modifier", multiplier - 1, 1));

    }


    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "ice_lance");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ICE_LANCE_MOVEMENT_MOD) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_LANCE_MOVEMENT_MOD);

    }

    public static class IceLanceBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityIceLance && entity.getOwner() != null) {
                World world = entity.world;
                if (world.isRemote) {
                    if (entity.ticksExisted > 1) {
                        //Particles at the front
                        if (entity.ticksExisted == 2)
                            AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, entity.getLookVec().scale(entity.height),
                                    (int) (entity.width * 15),
                                    entity.width, 0.01, entity.height, ParticleBuilder.Type.SNOW,
                                    AvatarEntityUtils.getMiddleOfEntity(entity), Vec3d.ZERO, entity.velocity().toMinecraft().scale(1 / 20F), true, 190, 235, 255, 140, false,
                                    entity.getLifeTime() + 20, entity.height * 3, true, -90);

                        //General ice particles
                        AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, entity.getLookVec().scale(entity.height),
                                (int) (entity.width * 20),
                                entity.width, 0.01, entity.height, ParticleBuilder.Type.FLASH,
                                AvatarEntityUtils.getMiddleOfEntity(entity), Vec3d.ZERO, Vec3d.ZERO, true, 140, 235, 255, 30, false,
                                6, entity.height * 1.5F, true, -90);
                        AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, entity.getLookVec().scale(entity.height),
                                (int) (entity.width * 15),
                                entity.width, 0.01, entity.height, ParticleBuilder.Type.FLASH,
                                AvatarEntityUtils.getMiddleOfEntity(entity), Vec3d.ZERO, Vec3d.ZERO, true, 140, 235, 255, 30, AvatarUtils.getRandomNumberInRange(1, 100) > 80,
                                12, entity.height, true, -90);

                        //misc snow particles
                        AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, entity.getLookVec().scale(entity.height),
                                (int) (entity.width * 30),
                                entity.width, 0.01, entity.height, ParticleBuilder.Type.SNOW,
                                AvatarEntityUtils.getMiddleOfEntity(entity), Vec3d.ZERO, Vec3d.ZERO, false, 190, 235, 255, 140, false,
                                12, entity.height * 3, true, -90);

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
