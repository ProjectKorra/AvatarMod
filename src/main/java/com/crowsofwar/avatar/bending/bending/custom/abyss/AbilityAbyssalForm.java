package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.custom.abyss.powermods.HyperFormPowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Objects;

import static com.crowsofwar.avatar.util.data.TickHandlerController.HYPER_FORM_HANDLER;

public class AbilityAbyssalForm extends Ability {

    public AbilityAbyssalForm() {
        super(Abyssbending.ID, "hyper_form");
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

        if (!data.hasTickHandler(HYPER_FORM_HANDLER)) {
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


                speedDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();
                strengthDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();
                healthDuration *= ctx.getPowerRatingDamageMod() * abilityData.getXpModifier();

                if (strengthLevel > 0) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, strengthDuration, strengthLevel - 1, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, strengthDuration, strengthLevel - 1, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, strengthDuration, strengthLevel - 1, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, strengthDuration, strengthLevel - 1, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, strengthDuration, strengthLevel - 1, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, strengthDuration, strengthLevel - 1, false, false));

                }
                if (healthLevel > 0)
                    entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, healthDuration, healthLevel - 1, false, false));

                if (speedLevel > 0)
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, speedDuration, speedLevel - 1, false, false));

                if (data.hasBendingId(getBendingId())) {

                    HyperFormPowerModifier modifier = new HyperFormPowerModifier();
                    modifier.setTicks(-1);

                    // Ignore warning; we know manager != null if they have the bending style
                    //noinspection ConstantConditions
                    data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

                }

                abilityData.addXp(getProperty(XP_USE, ctx).floatValue());
                data.addTickHandler(HYPER_FORM_HANDLER, ctx);

            }
        } else {
            data.removeTickHandler(HYPER_FORM_HANDLER, ctx);
            Objects.requireNonNull(data.getPowerRatingManager(getBendingId())).clearModifiers(ctx);
        }

    }


    @Override
    public int getBaseTier() {
        return 5;
    }
}
