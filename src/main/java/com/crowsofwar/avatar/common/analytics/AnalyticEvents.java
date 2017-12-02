package com.crowsofwar.avatar.common.analytics;

/**
 * @author CrowsOfWar
 */
public class AnalyticEvents {

	public static AnalyticEvent getAbilityExecutionEvent(String abilityName) {
		return new AnalyticEvent("AbilityExecute", abilityName);
	}

}
