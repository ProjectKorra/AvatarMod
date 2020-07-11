package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.util.data.ctx.BendingContext;

/**
 * @author CrowsOfWar
 */
public abstract class PowerRatingModifier {

	/**
	 * The amount of time left in this modifier. -1 for infinite modifier duration
	 */
	private int ticks = 20;

	public abstract double get(BendingContext ctx);

	/**
	 * Set the countdown for this modifier. Note that some modifiers may remove themselves under
	 * other conditions than the countdown being over. Also, some modifiers may ignore the
	 * countdown altogether.
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * Performed every tick; designed to remove the modifier when the time comes. Returns true if
	 * the modifier should be removed.
	 * <p>
	 * By default, modifiers are removed when a countdown is over.
	 */
	public boolean onUpdate(BendingContext ctx) {
		ticks--;
		return ticks <= 0;
	}

	/**
	 * Called when the power rating modifier has been activated.
	 */
	public void onAdded(BendingContext ctx) {
	}

	/**
	 * Called when the power rating modifier has been removed.
	 */
	public void onRemoval(BendingContext ctx) {
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == this.getClass();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
