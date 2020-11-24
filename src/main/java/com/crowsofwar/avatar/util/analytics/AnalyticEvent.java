package com.crowsofwar.avatar.util.analytics;

import javax.annotation.Nullable;

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

	@Nullable
	private final String label;

	// To be obtained with AnalyticEvents
	AnalyticEvent(String category, String name) {
		this(category, name, null);
	}

	AnalyticEvent(String category, String name, @Nullable String label) {
		this.category = category;
		this.name = name;
		this.creationTime = System.currentTimeMillis();
		this.label = label;
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

	@Nullable
	public String getLabel() {
		return label;
	}

	public boolean hasLabel() {
		return label != null;
	}

}
