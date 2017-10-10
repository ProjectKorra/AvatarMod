package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.AirJumpPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityRestore extends Ability {
    public AbilityRestore() {
        super(Earthbending.ID, "restore");
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        float chi = STATS_CONFIG.chiBuff;
        if (bender.consumeChi(chi)){
            AbilityData abilityData = data.getAbilityData(this);
            float xp = SKILLS_CONFIG.blockPlaced;

            entity.addPotionEffect(new PotionEffect (MobEffects.INSTANT_HEALTH, 1));
            data.getAbilityData("restore").addXp(xp);

            if (abilityData.getLevel()==1) {
                entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100));
                data.getAbilityData("restore").addXp(xp);
                chi *= 1.5f;
            }
                if (abilityData.getLevel()==2) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
                    entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100));
                    data.getAbilityData("restore").addXp(xp);
                    chi *= 2f;
                }
                    if (data.getAbilityData("restore").isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1));
                        entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 300, 1));
                        chi *= 2.5f;
                    }
                        if (data.getAbilityData("restore").isMasterPath(AbilityData.AbilityTreePath.SECOND)){
                            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
                            entity.addPotionEffect(new PotionEffect (MobEffects.INSTANT_HEALTH, 1));
                            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100));
                            entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 100 ));
                            entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 100));
                            chi *= 2.5f;
                        }

                    }
                data.getPowerRatingManager(getBendingId()).addModifier(new RestorePowerModifier());
                }

            }




