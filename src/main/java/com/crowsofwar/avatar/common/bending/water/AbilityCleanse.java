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
        float chi = STATS_CONFIG.chiBuff;

        if (bender.consumeChi(chi)) {
            AbilityData abilityData = data.getAbilityData(this);
            float xp = SKILLS_CONFIG.blockPlaced;

            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
            data.getAbilityData("cleanse").addXp(xp);

            if (abilityData.getLevel() == 1) {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200));
                data.getAbilityData("cleanse").addXp(xp);
                chi *= 1.5f;
            }
                if (abilityData.getLevel() == 2) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
                    entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
                    data.getAbilityData("cleanse").addXp(xp);
                    chi *= 2f;
                }
                     if (data.getAbilityData("cleanse").isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200));
                        entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
                         chi *= 2.5f;
                    }
                          if (data.getAbilityData("cleanse").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 2));
                            entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                            entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
                              chi *= 2.5f;
                    }

                }
                data.getPowerRatingManager(getBendingId()).addModifier(new CleansePowerModifier());
            }

        }

