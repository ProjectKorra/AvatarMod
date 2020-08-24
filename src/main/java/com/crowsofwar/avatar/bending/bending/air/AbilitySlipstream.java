package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;

import static com.crowsofwar.avatar.util.data.TickHandlerController.SLIPSTREAM_WALK_HANDLER;

public class AbilitySlipstream extends Ability {

    public static final String
            AIR_WALK = "walkOnAir",
            INVIS_CHANCE = "invisibleChance";

    public AbilitySlipstream() {
        super(Airbending.ID, "slipstream");
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        addProperties(STRENGTH_LEVEL, STRENGTH_DURATION, SPEED_LEVEL, SPEED_DURATION, JUMP_LEVEL, JUMP_DURATION, INVIS_CHANCE);
        addBooleanProperties(AIR_WALK);
    }

    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(this);
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();

        float chi = getChiCost(ctx);

        if (bender.consumeChi(chi)) {
            float xp = getProperty(XP_USE, ctx).floatValue();
            int duration = getProperty(DURATION, ctx).intValue();
            int speedLevel, speedDuration, jumpLevel, jumpDuration, strengthLevel, strengthDuration;

            duration *= (2 - abilityData.getDamageMult()) * abilityData.getXpModifier();
            speedLevel = getProperty(SPEED_LEVEL, ctx).intValue();
            speedDuration = getProperty(SPEED_DURATION, ctx).intValue();
            jumpLevel = getProperty(JUMP_LEVEL, ctx).intValue();
            jumpDuration = getProperty(JUMP_DURATION, ctx).intValue();
            strengthLevel = getProperty(STRENGTH_LEVEL, ctx).intValue();
            strengthDuration = getProperty(STRENGTH_DURATION, ctx).intValue();

            speedDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            jumpDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();
            strengthDuration *= abilityData.getDamageMult() * abilityData.getXpModifier();

            if (getBooleanProperty(AIR_WALK, ctx))
                data.addTickHandler(SLIPSTREAM_WALK_HANDLER, ctx);


            if (speedLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, speedDuration, speedLevel - 1, false, false));

            if (jumpLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, jumpDuration, jumpLevel - 1, false, false));

            if (strengthLevel > 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, strengthDuration, strengthLevel - 1, false, false));


            SlipstreamPowerModifier modifier = new SlipstreamPowerModifier();
            modifier.setTicks(duration);
            Objects.requireNonNull(data.getPowerRatingManager(getBendingId())).addModifier(modifier, ctx);
            abilityData.addXp(xp);
        }
        abilityData.setRegenBurnout(true);
        super.execute(ctx);

    }

    @Override
    public int getBaseTier() {
        return 5;
    }
}



