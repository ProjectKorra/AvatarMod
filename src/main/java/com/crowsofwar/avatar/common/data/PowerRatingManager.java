package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Manages the power rating
 *
 * @author CrowsOfWar
 */
public class PowerRatingManager {

	private final UUID bendingType;
	private List<PowerRatingModifier> modifiers;

	public PowerRatingManager(UUID bendingType) {
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
		modifier.setTicks(20);
	}

	public void removeModifier(PowerRatingModifier modifier) {
		modifiers.remove(modifier);
	}

	/**
	 * Modifiers are present for a finite amount of time. Counts down each modifier's time left and
	 * removes them if necessary.
	 */
	public void tickModifiers() {
		Iterator<PowerRatingModifier> iterator = modifiers.iterator();
		while (iterator.hasNext()) {
			PowerRatingModifier modifier = iterator.next();
			if (modifier.onUpdate()) {
				iterator.remove();
			}
		}
	}

}
