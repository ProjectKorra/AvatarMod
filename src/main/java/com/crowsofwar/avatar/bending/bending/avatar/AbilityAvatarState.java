package com.crowsofwar.avatar.bending.bending.avatar;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.avatar.powermods.AvatarStatePowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;
import java.util.stream.Collectors;

public class AbilityAvatarState extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityAvatarState() {
        super(Avatarbending.ID, "avatar_state");
    }

    @Override
    public void init() {
        super.init();
        addProperties(EFFECT_LEVEl);
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
            int level = getProperty(EFFECT_LEVEl, ctx).intValue();

            duration *= (2 - abilityData.getDamageMult()) * abilityData.getXpModifier();
            level = (int) powerModify(level, abilityData);


            //Air
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, level, false, false));
            //Water
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, level, false, false));
            //Earth
            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, level, false, false));
            //Fire
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, level, false, false));


            AvatarStatePowerModifier modifier = new AvatarStatePowerModifier();
            modifier.setTicks(duration * 8);
            for (BendingStyle style : BendingStyles.all()) {
                //Why do I need this
                if (style != null && data.getPowerRatingManager(style) != null && data.hasBendingId(style.getId()))
                    Objects.requireNonNull(data.getPowerRatingManager(style)).addModifier(modifier, ctx);
            }
            abilityData.addXp(xp);
        }
        abilityData.setRegenBurnout(true);
        super.execute(ctx);
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    @Override
    public boolean isUtility() {
        return true;
    }
}
