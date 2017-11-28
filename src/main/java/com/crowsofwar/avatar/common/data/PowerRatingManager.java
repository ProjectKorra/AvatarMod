package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.util.math.MathHelper;

import java.util.*;

/**
 * Manages the power rating
 *
 * @author CrowsOfWar
 */
public class PowerRatingManager {

	private final UUID bendingType;
	private Set<PowerRatingModifier> modifiers;

	public PowerRatingManager(UUID bendingType) {
		this.bendingType = bendingType;
		modifiers = new HashSet<>();
	}

	public double getRating(BendingContext ctx) {
		double result = 0;
		for (PowerRatingModifier modifier : modifiers) {
			result += modifier.get(ctx);
		}
		return MathHelper.clamp(result, -100, 100);
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

}
