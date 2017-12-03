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

	/**
	 * Different Visions are used as the player levels up to make it seem more powerful. Gets the
	 * appropriate vision to be used for the current level.
	 */
	private Vision getVision(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData("slipstream");
		switch (abilityData.getLevel()) {
			case 0:
			case 1:
				return Vision.SLIPSTREAM_WEAK;
			case 2:
				return Vision.SLIPSTREAM_MEDIUM;
			case 3:
			default:
				return Vision.SLIPSTREAM_POWERFUL;
		}

	}

	@Override
	public void onAdded(BendingContext ctx) {
			if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(getVision(ctx));
		}
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		Vision vision = ctx.getData().getVision();
		if (vision != null && vision.name().startsWith("SLIPSTREAM")) {
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
				invisiblityDuration = 40;
			}

			// Intermittently grant invisibility
			if (ctx.getBenderEntity().ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < invisibilityChance) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, invisiblityDuration);
					ctx.getBenderEntity().addPotionEffect(effect);
				}
			}

		}

		return super.onUpdate(ctx);
	}

}

