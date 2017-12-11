package com.crowsofwar.avatar.common.bending;

/**
 * Represents a numeric score of how well a bender is doing in a battle. This score is increased by
 * actions like damaging the enemy, and decreased by taking damage. It can be used for modifiers
 * and boosts; for example, airbenders get faster chi regeneration when having high battle
 * performance.
 * <p>
 * The actual battle performance score is obtained with {@link #getScore()} and is between -100 to
 * 100.
 *
 * @author CrowsOfWar
 */
public class BattlePerformance {

	/**
	 * Returns the numeric rating of how well the bender is doing in combat right now, ranging from
	 * -100 to 100 (0 is neutral).
	 */
	public double getScore() {
		return 0;
	}

}
