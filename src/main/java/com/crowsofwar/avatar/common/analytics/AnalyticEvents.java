package com.crowsofwar.avatar.common.analytics;

import net.minecraft.util.DamageSource;

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

	/**
	 * Event to be used when a player kills another player with an ability
	 */
	public static AnalyticEvent onPvpKillWithAbility(String damageSourceName) {
		return new AnalyticEvent("Killed", "player > player", damageSourceName);
	}

	/**
	 * Event to be used when a player kills any mob. Note: Not actually used if it was a regular mob
	 * killed with non-AV2 means.
	 */
	public static AnalyticEvent onMobKill(String mobName, DamageSource damageSource) {
		return new AnalyticEvent("Killed", "player > " + mobName, damageSource.damageType);
	}

	/**
	 * Event to be used when an AV2 mob kills another player with an ability
	 */
	public static AnalyticEvent onPlayerDeathWithMob(String mobName) {
		return new AnalyticEvent("Killed", mobName + " > player");
	}

	/**
	 * Event to be used when a player runs out of chi
	 */
	public static AnalyticEvent onOutOfChi() {
		return new AnalyticEvent("Misc", "out of chi");
	}

}
