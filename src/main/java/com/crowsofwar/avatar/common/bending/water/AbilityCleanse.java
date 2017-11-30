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
		if (abilityData.getLevel() == 1) {
			chi *= 1.5f;
		}
		if (abilityData.getLevel() == 2) {
			chi *= 2f;
		}
		if (abilityData.getLevel() == 3) {
			chi *= 2.5f;
		}

		if (bender.consumeChi(chi)) {

			// Duration: 5-10s
			int duration = abilityData.getLevel() < 2 ? 100 : 200;

			entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration));
			abilityData.addXp(SKILLS_CONFIG.buffUsed);

			if (abilityData.getLevel() == 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration));
			}

			if (abilityData.getLevel() == 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 2));
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			CleansePowerModifier modifier = new CleansePowerModifier();
			modifier.setTicks(duration);
			data.getPowerRatingManager(getBendingId()).addModifier(new CleansePowerModifier(), ctx);

		}

	}
}

