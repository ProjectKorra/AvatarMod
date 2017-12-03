package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.common.data.AbilityData;

/**
 * Allows one to obtain an {@link AnalyticEvent} object to be sent to google analytics.
 *
 * @author CrowsOfWar
 */
public class AnalyticEvents {

	public static AnalyticEvent getAbilityExecutionEvent(String abilityName, String levelDesc) {
		return new AnalyticEvent("Ability executed", abilityName, levelDesc);
	}

	public static AnalyticEvent getAbilityUpgradeEvent(String abilityName, int newLevel) {
		return new AnalyticEvent("Ability upgrades", abilityName, "lvl" + newLevel);
	}

	public static AnalyticEvent getAbilityMaxUpgradeEvent(String abilityName,
			AbilityData.AbilityTreePath path) {
		return new AnalyticEvent("Ability max upgrades", abilityName, "lvl4_" + path.ordinal());
	}

}
