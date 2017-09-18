package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

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
        World world = ctx.getWorld();
        if (bender.consumeChi(STATS_CONFIG.chiSlipstream)) {
            AbilityData abilityData = data.getAbilityData(this);
            int regenboost = abilityData.getLevel() - 1;
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, regenboost));
            if (abilityData.getLevel() == 2) {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, regenboost));
                entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200));
                if (abilityData.getLevel() == 3) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, regenboost));
                    entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200));
                    if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, regenboost));
                        entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200, 1));
                    }

                }
            }
        }
    }
}