package com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.light.AbilityHolyProtection;
import com.crowsofwar.avatar.bending.bending.custom.light.Lightbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityBuff;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.custom.light.AbilityHolyProtection.BLAST_LEVEL;
import static com.crowsofwar.avatar.bending.bending.custom.light.AbilityHolyProtection.SLOW_MULT;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_HOLY_PROTECTION;

public class HolyProtectionHandler extends TickHandler {
    public static final UUID HOLY_PROTECTION_MOVEMENT_MODIFIER_ID = UUID.randomUUID();

    public HolyProtectionHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("holy_protection");
        AbilityHolyProtection holyProtection = (AbilityHolyProtection) Abilities.get("holy_protection");

        float charge;
        //4 stages, max charge of 4.
        boolean shouldRemove = false;

        if (abilityData != null && holyProtection != null) {

            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = holyProtection.getProperty(EFFECT_DAMAGE, abilityData).floatValue();
            float slowMult = holyProtection.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = holyProtection.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = holyProtection.getProperty(EFFECT_RADIUS, abilityData).floatValue();
            float durationToFire = holyProtection.getProperty(CHARGE_TIME, abilityData).intValue();
            float speed = holyProtection.getProperty(SPEED, abilityData).floatValue() / 5;
            int performanceAmount = holyProtection.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = holyProtection.getExhaustion(abilityData);
            burnout = holyProtection.getBurnOut(abilityData);
            cooldown = holyProtection.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
//            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
//            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            charge = 4;
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

            if (world.isRemote && duration <= durationToFire) {
                Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, entity.getEyeHeight() / 2, 0);
                //Size starts small gets big
                float size = (float) ((1F / (inverseRadius / 2F)) * 0.5F + charge * 0.25F * abilityData.getXpModifier());
                //In case you forgot year 7 maths, radius * 2 * pi = circumference
                int particles = (int) (radius * Math.PI);
                int rings = (int) (Math.sqrt(radius) * 6);
                //Rings around the player (not around your finger; the police want you)
                // C u l t u r e
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(0.975F, 0.975F, 0.275F, 0.1F).glow(true)
                        .scale(size * 0.75F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).element(BendingStyles.get(Lightbending.ID))
                        .swirl(rings, particles, (float) inverseRadius, size / 1.5F, radius * 40,
                                (1F / size) * abilityData.getXpModifier() * charge, entity, world, false, pos,
                                ParticleBuilder.SwirlMotionType.IN, true, true);

                Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, inverseRadius / 2, 0);
                size = (float) (0.75F * inverseRadius);
                rings = (int) (inverseRadius * 8);
                particles = (int) (inverseRadius * 2 * Math.PI);

                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .element(BendingStyles.get(Lightbending.ID)).clr(0.95F, 0.95F, 0.275F, 0.075F).spawnEntity(entity).glow(true)
                        .swirl(rings, particles, (float) inverseRadius, size * 5, (float) (inverseRadius * 10F), (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);

            }
            world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.8F + world.rand.nextFloat() / 10);


            //Applies the proper status control based on level.
            if (charge >= holyProtection.getProperty(BLAST_LEVEL, abilityData).intValue()) {
                shouldRemove = true;
            }


            if (!data.hasStatusControl(RELEASE_HOLY_PROTECTION)) {

                EntityBuff buff = new EntityBuff(world);
                buff.setLifetime(120);
                buff.setOwner(entity);
                buff.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity));
                buff.setVelocity(Vec3d.ZERO);
                buff.setAbility(Objects.requireNonNull(Abilities.get("holy_protection")));
                buff.setElement(Lightbending.ID);
                buff.setRadius(radius);
                //Set behaviour

                int particleController = abilityData.getLevel() > 0 ? 48 - (6 * Math.max(abilityData.getLevel(), 0)) : 48;
                EntityShockwave shockwave = new EntityShockwave(world);
                shockwave.setOwner(entity);
                shockwave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity));
                shockwave.setRenderNormal(false);
                shockwave.setElement(Lightbending.ID);
                shockwave.setParticleSpeed(speed / 2);
                shockwave.setDamageSource("avatar_Light_sphere_shockwave");
                shockwave.setKnockbackMult(new Vec3d(knockBack, knockBack / 2, knockBack));
                shockwave.setDamage(damage);
                shockwave.setParticleAmount(2);
                shockwave.setRange(radius);
                shockwave.setSphere(true);
                shockwave.setTier(holyProtection.getCurrentTier(abilityData));
                shockwave.setXp(holyProtection.getProperty(XP_HIT, abilityData).floatValue());
                shockwave.setPerformanceAmount(performanceAmount);
                shockwave.setParticleSpeed(Math.min(knockBack / shockwaveSpeed * 1.5F, shockwaveSpeed));
                shockwave.setParticleController(particleController);
                shockwave.setAbility(Objects.requireNonNull(Abilities.get("holy_protection")));
                shockwave.setSpeed(speed / 4);
                shockwave.setBehaviour(new HolyProtectionShockwave());
                if (!world.isRemote) {
                    world.spawnEntity(shockwave);
                    world.spawnEntity(buff);
                }
                abilityData.addBurnout(burnout);
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);


                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HOLY_PROTECTION_MOVEMENT_MODIFIER_ID);

                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                        SoundCategory.BLOCKS, 1, 0.5F);

                return true;
            }
            return !data.hasStatusControl(RELEASE_HOLY_PROTECTION);
        } else {
            return true;
        }
    }


    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(HOLY_PROTECTION_MOVEMENT_MODIFIER_ID);

        moveSpeed.applyModifier(new AttributeModifier(HOLY_PROTECTION_MOVEMENT_MODIFIER_ID,
                "Holy Protection charge modifier", multiplier - 1, 1));

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "holy_protection");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HOLY_PROTECTION_MOVEMENT_MODIFIER_ID) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HOLY_PROTECTION_MOVEMENT_MODIFIER_ID);

    }


    //TODO: Add code in EntityBuff for a swirl shield (like air bubble)
    public static class HolyProtectionShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityShockwave && entity.getOwner() != null) {
                World world = entity.world;
                if (world.isRemote && entity.ticksExisted <= 2) {
                    float maxRadius = (float) ((EntityShockwave) entity).getRange();
                    int rings = (int) (maxRadius * 4 + 6);
                    float size = (float) (Math.sqrt(maxRadius));
                    int particles = (int) (Math.sqrt(maxRadius) / 1.5F * Math.PI);
                    Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(entity);
                    for (int i = 0; i < 100 * particles; i++)
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size)
                                .time(16).collide(true).glow(true).scale(size)
                                .element(BendingStyles.get(entity.getElement())).clr(0.95F, 0.95F, 0.3F, 0.075F).spawnEntity(entity.getOwner())
                                .pos(centre).vel(world.rand.nextGaussian() * entity.getParticleSpeed(),
                                world.rand.nextGaussian() * entity.getParticleSpeed(), world.rand.nextGaussian() * entity.getParticleSpeed())
                                .spawn(world);


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

    //To lazy to do behaviour crap rn
//    public static class HolyProtectionBuffBehaviour extends Behavior<EntityBuff> {
//
//        @Override
//        public Behavior onUpdate(EntityBuff entity) {
//            if (entity.getOwner() != null) {
//                List<EntityLivingBase> targets = entity.world.getEntitiesWithinAABB(EntityLivingBase.class,
//                        entity.getEntityBoundingBox().grow(entity.getRadius()));
//                if (!targets.isEmpty()) {
//                    for (EntityLivingBase target : targets) {
//                        if (!DamageUtils.canDamage(entity.getOwner(), target)) {
//                            target.addPotionEffect(new PotionEffect(MobEffects.SPEED,
//                                    2, 120));
//                            target.addPotionEffect(new PotionEffect(MobEffects.GLOWING,
//                                    2, 120));
//                            target.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,
//                                    0, 120));
//                        }
//                    }
//                }
//            }
//            return this;
//        }
//
//        @Override
//        public void fromBytes(PacketBuffer buf) {
//
//        }
//
//        @Override
//        public void toBytes(PacketBuffer buf) {
//
//        }
//
//        @Override
//        public void load(NBTTagCompound nbt) {
//
//        }
//
//        @Override
//        public void save(NBTTagCompound nbt) {
//
//        }
//    }
}
