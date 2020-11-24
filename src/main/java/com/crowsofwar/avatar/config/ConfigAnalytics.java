package com.crowsofwar.avatar.config;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import java.util.UUID;

public class ConfigAnalytics {

	public static final ConfigAnalytics ANALYTICS_CONFIG = new ConfigAnalytics();
	public UUID analyticsId;
	@Load
	private boolean analyticsEnabled = true;
	@Load
	public boolean displayAnalyticsWarning = true;
	@Load
	private String analyticsIdStr = UUID.randomUUID().toString();

	private boolean inAnalyticsGroup = false;

	private ConfigAnalytics() {
	}

	public static void load() {
		ConfigLoader.load(ANALYTICS_CONFIG, "avatar/analytics.yml", true);
		ANALYTICS_CONFIG.setupAnalyticsId();
	}

	private void setupAnalyticsId() {

		try {

			analyticsId = UUID.fromString(analyticsIdStr);
			// Choose 1/256th of players to send analytics
			inAnalyticsGroup = analyticsId.toString().startsWith("00");

		} catch (IllegalArgumentException e) {

			analyticsId = UUID.randomUUID();
			analyticsEnabled = false;
			AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid analytics id string, " +
					"analytics temporarily disabled. Suggested to edit analytics.yml and set " +
					"analyticsIdStr to '" + UUID.randomUUID().toString() + "'");

		}

	}

	public void optOutAnalytics() {
		analyticsEnabled = false;
		ConfigLoader.save(this, "avatar/analytics.yml", true);
	}

	public void dontShowAnalyticsWarning() {
		displayAnalyticsWarning = false;
		ConfigLoader.save(this, "avatar/analytics.yml", true);
	}

	/**
	 * Returns whether to send analytics. This is a combination of whether the user opted in, and
	 * also whether they are part of the small group that sends analytics (1%).
	 */
	public boolean isAnalyticsEnabled() {
		return analyticsEnabled && inAnalyticsGroup;
	}

}
