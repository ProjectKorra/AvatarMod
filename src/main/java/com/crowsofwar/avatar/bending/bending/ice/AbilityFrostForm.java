package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.ice.powermods.FrostPowerModifier;
import com.crowsofwar.avatar.entity.EntityLightOrb;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Objects;

import static com.crowsofwar.avatar.util.data.TickHandlerController.FROST_FORM_HANDLER;

public class AbilityFrostForm extends Ability {

    public AbilityFrostForm() {
        super(Icebending.ID, "frost_form");
    }

    @Override
    public void init() {
        super.init();
        addProperties(STRENGTH_LEVEL, STRENGTH_DURATION, HEALTH_LEVEL, HEALTH_DURATION, SPEED_LEVEL, SPEED_DURATION,
                R, G, B, FADE_R, FADE_G, FADE_B);
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData(this);

        float chi = getChiCost(ctx);
        //TODO: Literally all of this

        if (!data.hasTickHandler(FROST_FORM_HANDLER)) {
            if (bender.consumeChi(chi)) {

                //Buff abilities are unaffected by powerrating, otherwise they'd be stupid good
                int duration = getProperty(DURATION, ctx).intValue();
                int strengthLevel, strengthDuration, healthLevel, healthDuration, speedLevel, speedDuration;
                strengthLevel = getProperty(STRENGTH_LEVEL, ctx).intValue();
                strengthDuration = getProperty(STRENGTH_DURATION, ctx).intValue();
                healthLevel = getProperty(HEALTH_LEVEL, ctx).intValue();
                healthDuration = getProperty(HEALTH_DURATION, ctx).intValue();
                speedLevel = getProperty(SPEED_LEVEL, ctx).intValue();
                speedDuration = getProperty(SPEED_DURATION, ctx).intValue();

                int lightRadius = 5;

                if (abilityData.getLevel() == 1) {
                    lightRadius = 7;
                }

                if (abilityData.getLevel() == 2) {
                    lightRadius = 10;
                }

                if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                    lightRadius = 9;
                }

                if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                    lightRadius = 12;
                }

                speedDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();
                strengthDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();
                healthDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();

                if (strengthLevel > 0)
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, strengthDuration, strengthLevel - 1, false, false));

                if (healthLevel > 0)
                    entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, healthDuration, healthLevel - 1, false, false));

                if (speedLevel > 0)
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, speedDuration, speedLevel - 1, false, false));

                if (data.hasBendingId(getBendingId())) {

                    FrostPowerModifier modifier = new FrostPowerModifier();
                    modifier.setTicks(-1);

                    // Ignore warning; we know manager != null if they have the bending style
                    //noinspection ConstantConditions
                    data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

                }

//            EntityLightOrb orb = new EntityLightOrb(world);
//            orb.setOwner(entity);
//            orb.setAbility(this);
//            orb.setPosition(new Vec3d(entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2, entity.posZ));
//            orb.setOrbSize(0.005F);
//            orb.setLifeTime(duration);
//            orb.setColor(1F, 0.5F, 0F, 3F);
//            orb.setLightRadius(lightRadius);
//            orb.setEmittingEntity(entity);
//            orb.setBehavior(new CorruptLightOrbBehaviour());
//            orb.setType(EntityLightOrb.EnumType.COLOR_CUBE);
//            if (!world.isRemote)
//                world.spawnEntity(orb);
                abilityData.addXp(getProperty(XP_USE, ctx).floatValue());
                data.addTickHandler(FROST_FORM_HANDLER, ctx);
            }
        } else {
            data.removeTickHandler(FROST_FORM_HANDLER, ctx);
            Objects.requireNonNull(data.getPowerRatingManager(getBendingId())).clearModifiers(ctx);
        }

    }


    @Override
    public int getBaseTier() {
        return 5;
    }

    public static class FrostLightOrbBehaviour extends LightOrbBehavior.FollowPlayer {
        @Override
        public Behavior<EntityLightOrb> onUpdate(EntityLightOrb entity) {
            super.onUpdate(entity);
//            EntityLivingBase emitter = entity.getOwner();
//            if (emitter != null) {
//                assert emitter instanceof EntityPlayer || emitter instanceof EntityBender;
//                Bender b = Bender.get(emitter);
//                if (b != null && BendingData.getFromEntity(emitter) != null && entity.ticksExisted > 1) {
//                    if (!Objects.requireNonNull(b.getData().getPowerRatingManager(Darkbending.ID)).hasModifier(CorruptPowerModifier.class)) {
//                        entity.setDead();
//                    }
//                }
//                int lightRadius = 5;
//                //Stops constant spam and calculations
//                if (entity.ticksExisted == 1) {
//                    AbilityData aD = AbilityData.get(emitter, "corrupt");
//                    if (aD != null) {
//                        int level = aD.getLevel();
//                        if (level >= 1) {
//                            lightRadius = 7;
//                        }
//                        if (level >= 2) {
//                            lightRadius = 10;
//                        }
//                        if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
//                            lightRadius = 9;
//                        }
//                        if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
//                            lightRadius = 12;
//                        }
//                    }
//                }
//                if (entity.getEntityWorld().isRemote) entity.setLightRadius(lightRadius + (int) (Math.random() * 5));
//                return this;
//            } else entity.setDead();
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {
            super.fromBytes(buf);
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            super.toBytes(buf);
        }

        @Override
        public void load(NBTTagCompound nbt) {
            super.load(nbt);
        }

        @Override
        public void save(NBTTagCompound nbt) {
            super.save(nbt);
        }
    }
}

