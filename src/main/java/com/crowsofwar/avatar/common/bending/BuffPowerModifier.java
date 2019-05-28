package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.config.ConfigClient;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

/**
 * "Buff abilities", abilities which apply temporary bending and other boosts,
 * usually have a power rating modifier to temporarily increase bending power.
 * Therefore, they all have power rating modifiers. This is the superclass for
 * all buff ability related power modifiers.
 *
 * @author CrowsOfWar
 */
public abstract class BuffPowerModifier extends PowerRatingModifier {

	/**
	 * As the player levels up, buff abilities typically apply more "powerful"
	 * looking vision s, in which the visual effects/distortions are the same, but
	 * of higher intensity. Returns the different Vision s to be applied when the
	 * player is at different ability levels.
	 * <p>
	 * This should return a 3-element array, with the first element being the weak
	 * vision to be used at levels I and II, the second element being the medium
	 * vision to be used at level III, and finally the third element being the
	 * powerful vision to be used at level IV.
	 */
	protected abstract Vision[] getVisions();

	protected abstract String getAbilityName();

	private boolean useSlipstreams = ConfigClient.CLIENT_CONFIG.shaderSettings.useSlipstreamShaders;

	private boolean useCleanses = ConfigClient.CLIENT_CONFIG.shaderSettings.useCleanseShaders;

	private boolean useRestores = ConfigClient.CLIENT_CONFIG.shaderSettings.useRestoreShaders;

	private boolean usePurifys = ConfigClient.CLIENT_CONFIG.shaderSettings.usePurifyShaders;

	private Vision getVision(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData(getAbilityName());

		// Handle disabling
		if (!useSlipstreams && abilityData.getAbility() == Abilities.get("slipstream")) {
			return null;
		} else if (!useCleanses && abilityData.getAbility() == Abilities.get("cleanse")) {
			return null;
		} else if (!useRestores && abilityData.getAbility() == Abilities.get("restore")) {
			return null;
		} else if (!usePurifys && abilityData.getAbility() == Abilities.get("purify")) {
			return null;
		}

		switch (abilityData.getLevel()) {
		case -1:
		case 0:
		case 1:
			if (getVisions()[0] != null) {
				return getVisions()[0];
			}
		case 2:
			if (getVisions()[1] != null) {
				return getVisions()[1];
			}
		case 3:
		default:
			if (getVisions()[2] != null) {
				return getVisions()[2];
			} else
				return getVisions()[0];
		}

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			if (getVision(ctx) != null && getVisions()[0] != null && getVisions()[1] != null
					&& getVisions()[2] != null) {
				ctx.getData().setVision(getVision(ctx));
			}

		}
		return super.onUpdate(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		Vision[] visions = getVisions();
		Vision vision = ctx.getData().getVision();
		if (vision != null && visions != null) {
			if (vision == visions[0] || vision == visions[1] || vision == visions[2]) {
				ctx.getData().setVision(null);
			}
			super.onRemoval(ctx);
		}
	}

}
