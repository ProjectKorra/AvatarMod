package com.crowsofwar.avatar.common.bending.air;

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

public class AbilitySlipstream extends Ability {

	public AbilitySlipstream() {
		super(Airbending.ID, "slipstream");
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(this);
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		float chi = STATS_CONFIG.chiBuff;

		if (abilityData.getLevel() == 1) {
			chi *= 1.5f;
		}
		if (abilityData.getLevel() == 2) {
			chi *= 2f;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			chi *= 2.5F;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi *= 2.5F;
		}
		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
			data.getAbilityData("slipstream").addXp(xp);

			if (abilityData.getLevel() == 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
				data.getAbilityData("slipstream").addXp(xp - 0.5F);
			}

			if (abilityData.getLevel() == 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 100, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1));
				data.getAbilityData("slipstream").addXp(xp - 1.0F);
			}

			if (data.getAbilityData("slipstream").isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 100));
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 100, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1));
			}

			if (data.getAbilityData("slipstream").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 200));
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 200, 2));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 2));
			}

			SlipstreamPowerModifier modifier = new SlipstreamPowerModifier();
			modifier.setTicks(20 + (20 * abilityData.getLevel()));
			data.getPowerRatingManager(getBendingId()).addModifier(new SlipstreamPowerModifier(),
					ctx);

		}

	}
}



