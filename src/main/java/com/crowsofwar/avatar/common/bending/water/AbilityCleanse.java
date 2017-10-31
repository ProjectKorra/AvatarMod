package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;


import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityCleanse extends Ability {

    public AbilityCleanse() {
        super(Waterbending.ID, "cleanse");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(this);
        float chi = STATS_CONFIG.chiBuff;
        if (abilityData.getLevel()==1){
            chi *= 1.5f;
        }
        if (abilityData.getLevel()==2){
            chi *= 2f;
        }
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)){
            chi *= 2.5F;
        }
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)){
            chi *= 2.5F;
        }

        if (bender.consumeChi(chi)) {
            float xp = SKILLS_CONFIG.buffUsed;

            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100));
            data.getAbilityData("cleanse").addXp(xp);

            if (abilityData.getLevel() == 1) {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 100));
                data.getAbilityData("cleanse").addXp(xp);
            }
            if (abilityData.getLevel() == 2) {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 100));
                data.getAbilityData("cleanse").addXp(xp);
            }
            if (data.getAbilityData("cleanse").isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
            }
            if (data.getAbilityData("cleanse").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 2));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
            }
            CleansePowerModifier modifier = new CleansePowerModifier();
            modifier.setTicks(20+(20*abilityData.getLevel()));
            data.getPowerRatingManager(getBendingId()).addModifier(new CleansePowerModifier());
            }
        }
    }

