package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class SlipstreamPowerModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilitySlipstream());

		double modifier = 20 + 8 * abilityData.getLevel();
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 60;
		}

		return modifier;

	}

	@Override
	public void onAdded(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(Vision.SLIPSTREAM);
		}
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		if (ctx.getData().getVision() == Vision.SLIPSTREAM) {
			ctx.getData().setVision(null);
		}
	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		AbilityData data = ctx.getData().getAbilityData("slipstream");

		if (data.getLevel() >= 2) {

			double invisibilityChance = 0.3;
			int invisiblityDuration = 30;

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				invisibilityChance = 0.4;
				invisibilityChance = 40;
			}

			// Intermittently grant invisibility
			if (ctx.getBenderEntity().ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < 0.3) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, 40);
					ctx.getBenderEntity().addPotionEffect(effect);
				}
			}

		}

		return super.onUpdate(ctx);
	}

}

