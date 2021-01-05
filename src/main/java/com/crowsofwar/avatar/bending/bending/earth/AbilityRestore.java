package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.powermods.RestorePowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.util.data.TickHandlerController.RESTORE_PARTICLE_SPAWNER;

public class AbilityRestore extends Ability {

    public AbilityRestore() {
        super(Earthbending.ID, "restore");
    }

    @Override
    public void init() {
        super.init();
        addProperties(RADIUS, STRENGTH_DURATION, STRENGTH_LEVEL, SLOWNESS_DURATION, SLOWNESS_LEVEL, RESISTANCE_DURATION, RESISTANCE_LEVEL, REGEN_DURATION, REGEN_LEVEL,
                SATURATION_DURATION, SATURATION_LEVEL, CHI_BOOST, CHI_REGEN_BOOST);
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    // Note: Restore does not use power rating since it's designed as a buff ability, and it could result in
    // "overpowering" for buffs to enhance more buffs

    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(this);
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();

        if (bender.consumeChi(getChiCost(ctx)) && Earthbending.getClosestEarthbendableBlock(entity, ctx, this, 2) != null) {

            abilityData.addXp(getProperty(XP_USE, ctx).floatValue());

            // 3s + 1.5s per level
            int duration = getProperty(DURATION, ctx).intValue();
            int resistanceLevel, slownessLevel, regenLevel, saturationLevel, strengthLevel,
                    resistanceDuration, slownessDuration, regenDuration, saturationDuration, strengthDuration;

            resistanceLevel = getProperty(RESISTANCE_LEVEL, ctx).intValue();
            slownessLevel = getProperty(SLOWNESS_LEVEL, ctx).intValue();
            regenLevel = getProperty(REGEN_LEVEL, ctx).intValue();
            saturationLevel = getProperty(SATURATION_LEVEL, ctx).intValue();
            strengthLevel = getProperty(STRENGTH_LEVEL, ctx).intValue();

            resistanceDuration = getProperty(RESISTANCE_DURATION, ctx).intValue();
            slownessDuration = getProperty(SLOWNESS_DURATION, ctx).intValue();
            regenDuration = getProperty(REGEN_DURATION, ctx).intValue();
            saturationDuration = getProperty(SATURATION_DURATION, ctx).intValue();
            strengthDuration = getProperty(STRENGTH_DURATION, ctx).intValue();

            duration *= abilityData.getDamageMult() * abilityData.getXpModifier();

            regenDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            slownessDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            regenDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            saturationDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            strengthDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();

            // Add potion effects
            if (resistanceLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, resistanceDuration, resistanceLevel - 1));
            if (slownessLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slownessDuration, slownessLevel - 1));
            if (regenLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, regenDuration, regenLevel - 1));
            if (strengthLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, strengthDuration, resistanceLevel));
            if (saturationLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, saturationDuration, saturationLevel - 1));

            // Apply power rating modifier

            RestorePowerModifier modifier = new RestorePowerModifier();
            modifier.setTicks(duration);

            // Ignore warning; we know manager != null if they have the bending style
            //noinspection ConstantConditions
            data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);
            data.addTickHandler(RESTORE_PARTICLE_SPAWNER, ctx);

        }
    }

    @Override
    public int getBaseTier() {
        return 5;
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        int coolDown = 160;

        if (ctx.getLevel() == 1) {
            coolDown = 150;
        }
        if (ctx.getLevel() == 2) {
            coolDown = 140;
        }
        if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
            coolDown = 130;
        }
        if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
            coolDown = 140;
        }

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            coolDown = 0;
        }
        return coolDown;
    }
}



