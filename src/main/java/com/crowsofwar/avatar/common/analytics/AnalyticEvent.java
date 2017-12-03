package com.crowsofwar.avatar.common.analytics;

/**
 * Keeps track of a single event that can be recorded as a statistic. This can then be sent online
 * to google analytics. An AnalyticEvent object is created every time the actual event happens, and
 * can be obtained with {@link AnalyticEvents}.
 * <p>
 * To send the analytic, use {@link AvatarAnalytics#pushEvent(AnalyticEvent)}.
 *
 * @author CrowsOfWar
 */
public class AnalyticEvent {

	private final String category, name;
	private final long creationTime;

	// To be obtained with AnalyticEvents
	AnalyticEvent(String category, String name) {
		this.category = category;
		this.name = name;
		this.creationTime = System.currentTimeMillis();
	}

	public String getCategory() {
		return category;
	}

	public String getAction() {
		return name;
	}

	public long getCreationTime() {
		return creationTime;
	}

}
