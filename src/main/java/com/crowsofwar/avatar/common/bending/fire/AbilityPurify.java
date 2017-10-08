package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.AirJumpPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityPurify extends Ability {
    public AbilityPurify() {
        super(Firebending.ID, "purify");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        if (bender.consumeChi(STATS_CONFIG.chiSlipstream)) {
            float xp = SKILLS_CONFIG.blockPlaced;
            AbilityData abilityData = data.getAbilityData(this);

            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
            data.getAbilityData("purify").addXp(xp);




            if (abilityData.getLevel()==1) {

                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
                data.getAbilityData("purify").addXp(xp-0.5F);

            }

            if (abilityData.getLevel()==2) {

                entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
                data.getAbilityData("purify").addXp(xp - 1.0F);
            }


            if (data.getAbilityData("purify").isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1));
            }

            if (data.getAbilityData("purify").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                entity.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 200));
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200));
            }

        }
        data.getPowerRatingManager(getBendingId()).addModifier(new PurifyPowerModifier());

    }
}
