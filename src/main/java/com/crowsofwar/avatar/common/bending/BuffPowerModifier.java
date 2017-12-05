package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public abstract class BuffPowerModifier extends PowerRatingModifier {

	/**
	 * As the player levels up, buff abilities typically apply more "powerful" looking vision
	 * shaders, in which the visual effects/distortions are the same, but of higher intensity.
	 * Returns the different Vision shaders to be applied when the player is at different ability
	 * levels.
	 * <p>
	 * This should return a 3-element array, with the first element being the weak vision shader
	 * to be used at levels I and II, the second element being the medium vision shader to be
	 * used at level III, and finally the third element being the powerful vision shader to be
	 * used at level IV.
	 */
	protected abstract Vision[] getVisions();

	protected abstract String getAbilityName();

	private Vision getVision(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData(getAbilityName());
		switch (abilityData.getLevel()) {
			case 0:
			case 1:
				return getVisions()[0];
			case 2:
				return getVisions()[1];
			case 3:
			default:
				return getVisions()[2];
		}

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(getVision(ctx));
		}
		return super.onUpdate(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		Vision[] visions = getVisions();
		Vision vision = ctx.getData().getVision();
		if (vision == visions[0] || vision == visions[1] || vision == visions[2]) {
			ctx.getData().setVision(null);
		}
		super.onRemoval(ctx);
	}

}

