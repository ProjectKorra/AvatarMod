package com.crowsofwar.avatar.common.config;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import java.util.UUID;

public class ConfigAnalytics {

	public static final ConfigAnalytics ANALYTICS_CONFIG = new ConfigAnalytics();

	private ConfigAnalytics() {}

	@Load
	private String analyticsIdStr = UUID.randomUUID().toString();
	public UUID analyicsId;

	@Load
	public boolean analyticsEnabled = true;

	public static void load() {
		ConfigLoader.load(ANALYTICS_CONFIG, "avatar/analytics.yml", true);
		ANALYTICS_CONFIG.setupAnalyticsId();
	}

	private void setupAnalyticsId() {

		try {

			analyicsId = UUID.fromString(analyticsIdStr);
			
		} catch (IllegalArgumentException e) {

			analyicsId = UUID.randomUUID();
			analyticsEnabled = false;
			AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid analytics id string, " +
					"analytics temporarily disabled. Suggested to edit analytics.yml and set " +
					"analyticsIdStr to '" + UUID.randomUUID().toString() + "'");

		}

	}

}
