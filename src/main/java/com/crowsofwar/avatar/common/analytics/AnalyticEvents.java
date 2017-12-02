package com.crowsofwar.avatar.common.analytics;

/**
 * Allows one to obtain an {@link AnalyticEvent} object to be sent to google analytics.
 *
 * @author CrowsOfWar
 */
public class AnalyticEvents {

	public static AnalyticEvent getAbilityExecutionEvent(String abilityName) {
		return new AnalyticEvent("AbilityExecute", abilityName);
	}

}
