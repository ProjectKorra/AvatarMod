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

			// 4s base + 1s per level
			int duration = 80 + 20 * abilityData.getLevel();

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
			}

			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, effectLevel));
			data.getAbilityData("slipstream").addXp(xp);

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, effectLevel));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration));
			}

			SlipstreamPowerModifier modifier = new SlipstreamPowerModifier();
			modifier.setTicks(duration);
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

		}

	}
}



