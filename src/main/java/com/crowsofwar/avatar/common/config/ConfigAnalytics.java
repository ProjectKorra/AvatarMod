package com.crowsofwar.avatar.common.config;

import com.crowsofwar.gorecore.config.ConfigLoader;

public class ConfigAnalytics {

	public static final ConfigAnalytics ANALYTICS_CONFIG = new ConfigAnalytics();

	private ConfigAnalytics() {}

	public static void load() {
		ConfigLoader.load(ANALYTICS_CONFIG, "avatar/analytics.yml");
	}

}
