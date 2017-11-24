package com.crowsofwar.avatar.common.bending.fire;

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

public class AbilityPurify extends Ability {

	public AbilityPurify() {
		super(Firebending.ID, "purify");
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		AbilityData abilityData = data.getAbilityData(this);

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1){
			chi *= 1.5f;
		} else if (abilityData.getLevel() ==2 ){
			chi *= 2f;
		} else if (abilityData.getLevel() == 3) {
			chi *= 2.5f;
		}

		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
			data.getAbilityData("purify").addXp(xp);

			if (abilityData.getLevel() == 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
				data.getAbilityData("purify").addXp(xp);
			}

			if (abilityData.getLevel() == 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
				data.getAbilityData("purify").addXp(xp);
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

			PurifyPowerModifier modifier = new PurifyPowerModifier();
			modifier.setTicks(20+(20*abilityData.getLevel()));
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);
		}
	}
}
