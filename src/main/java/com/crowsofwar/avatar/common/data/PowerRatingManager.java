package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the power rating
 *
 * @author CrowsOfWar
 */
public class PowerRatingManager {

	private final UUID bendingType;
	private Set<PowerRatingModifier> modifiers;

	private double cachedValue;
	private boolean useCache;

	public PowerRatingManager(UUID bendingType) {
		this.bendingType = bendingType;
		modifiers = new HashSet<>();

		cachedValue = 0;
		useCache = false;
	}

	public double getRating(BendingContext ctx) {

		if (useCache) {
			return cachedValue;
		}

		double result = 0;
		for (PowerRatingModifier modifier : modifiers) {
			result += modifier.get(ctx);
		}
		return MathHelper.clamp(result, -1000, 1000);
	}

	public void addModifier(PowerRatingModifier modifier, BendingContext ctx) {
		modifier.onAdded(ctx);
		modifiers.add(modifier);
	}

	public void removeModifier(PowerRatingModifier modifier, BendingContext ctx) {
		modifier.onRemoval(ctx);
		modifiers.remove(modifier);
	}

	public boolean hasModifier(Class<? extends PowerRatingModifier> modifier) {
		for (PowerRatingModifier mod : modifiers) {
			if (mod.getClass() == modifier) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Modifiers are present for a finite amount of time. Counts down each modifier's time left and
	 * removes them if necessary.
	 */
	public void tickModifiers(BendingContext ctx) {
		Iterator<PowerRatingModifier> iterator = modifiers.iterator();
		//noinspection Java8CollectionRemoveIf
		while (iterator.hasNext()) {
			PowerRatingModifier modifier = iterator.next();
			if (modifier.onUpdate(ctx)) {
				modifier.onRemoval(ctx);
				iterator.remove();
			}
		}
	}

	/**
	 * Removes all power rating modifiers from this manager.
	 */
	public void clearModifiers(BendingContext ctx) {
		Iterator<PowerRatingModifier> iterator = modifiers.iterator();
		while (iterator.hasNext()) {
			PowerRatingModifier modifier = iterator.next();
			modifier.onRemoval(ctx);
			iterator.remove();
		}
	}

	public UUID getBendingType() {
		return bendingType;
	}

	/**
	 * Sets the cached rating value so that the manager ignores all modifiers, only returning this
	 * desired value. Only to be used by the client, where it doesn't know anything about the
	 * modifiers, only the final value (which is calculated by the server).
	 */
	public void setCachedRatingValue(double value) {
		cachedValue = value;
		useCache = true;
	}

}
