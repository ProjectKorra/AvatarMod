package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages the power rating
 *
 * @author CrowsOfWar
 */
public class PowerRating {

	private final UUID bendingType;
	private List<PowerRatingModifier> modifiers;

	public PowerRating(UUID bendingType) {
		this.bendingType = bendingType;
		modifiers = new ArrayList<>();
	}

	public double getRating(BendingContext ctx) {
		double result = 0;
		for (PowerRatingModifier modifier : modifiers) {
			result += modifier.get(ctx);
		}
		return result;
	}

	public void addModifier(PowerRatingModifier modifier) {
		modifiers.add(modifier);
	}

	public void removeModifier(PowerRatingModifier modifier) {
		modifiers.remove(modifier);
	}

}
