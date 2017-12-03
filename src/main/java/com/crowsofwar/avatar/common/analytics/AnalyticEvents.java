package com.crowsofwar.avatar.common.analytics;

/**
 * Allows one to obtain an {@link AnalyticEvent} object to be sent to google analytics.
 *
 * @author CrowsOfWar
 */
public class AnalyticEvents {

	public static AnalyticEvent getAbilityExecutionEvent(String abilityName, String levelDesc) {
		return new AnalyticEvent("Ability executed", abilityName, levelDesc);
	}

	public static AnalyticEvent getAbilityUpgradeEvent(String abilityName, String levelDesc) {
		return new AnalyticEvent("Ability upgrades", abilityName, levelDesc);
	}

}
