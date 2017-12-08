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
		String category = "Ability upgrades";
		if (levelDesc.equals("lvl1")) {
			category = "Ability unlocked";
		}
		return new AnalyticEvent(category, abilityName, levelDesc);
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

	/**
	 * Event to be used when a player sleeps in bed and restores chi
	 */
	public static AnalyticEvent onSleepRestoration() {
		return new AnalyticEvent("Misc", "Restored chi from sleep");
	}

	/**
	 * Event to be used when a player tames a bison
	 */
	public static AnalyticEvent onBisonTamed() {
		return new AnalyticEvent("Bison", "tamed");
	}

	/**
	 * Event to be used when a sky bison defends/helps its owner against an enemy
	 */
	public static AnalyticEvent onBisonDefend(String targetEntity) {
		return new AnalyticEvent("Bison", "defend vs " + targetEntity);
	}

	/**
	 * Event to be used when a player trades with an NPC bender to get a scroll
	 */
	public static AnalyticEvent onNpcTrade() {
		return new AnalyticEvent("Scrolls", "NPC trade");
	}

	/**
	 * Event to be used when a player exhausts an NPC's trading supply of scrolls
	 */
	public static AnalyticEvent onNpcNoScrolls() {
		return new AnalyticEvent("Misc", "NPC supply exhausted");
	}

	/**
	 * Event to be used when a mob drops a scroll
	 */
	public static AnalyticEvent onMobScrollDrop(String mobName, String scrollType) {
		return new AnalyticEvent("Scrolls", "mob dropped scroll", mobName + " dropped "
				+ scrollType);
	}

	/**
	 * Event to be used when a player used the /avatar command
	 */
	public static AnalyticEvent onAvatarCommand() {
		return new AnalyticEvent("Misc", "used /avatar command");
	}

	/**
	 * Event to be used when one player gave another player a scroll
	 */
	public static AnalyticEvent onScrollShared(String scrollType) {
		return new AnalyticEvent("Scrolls", "shared scroll", scrollType);
	}

}
