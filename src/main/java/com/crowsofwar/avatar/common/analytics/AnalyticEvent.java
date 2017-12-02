package com.crowsofwar.avatar.common.analytics;

public class AnalyticEvent {

	private final String category, name;

	public AnalyticEvent(String category, String name) {
		this.category = category;
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public String getAction() {
		return name;
	}

}
