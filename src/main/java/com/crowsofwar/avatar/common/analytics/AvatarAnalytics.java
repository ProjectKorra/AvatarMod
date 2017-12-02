package com.crowsofwar.avatar.common.analytics;

/**
 * @author CrowsOfWar
 */
public class AvatarAnalytics {

	public static final AvatarAnalytics INSTANCE = new AvatarAnalytics();

	private final EventAnalytics events;

	public AvatarAnalytics() {
		events = new EventAnalytics();
	}

	public void pushEvent(AnalyticEvent event) {
		events.pushEvent(event);
	}

	public void uploadEvents() {
		events.uploadEvents();
	}

	public void pushEvents(AnalyticEvent... events) {
		this.events.pushEvents(events);
	}

}
