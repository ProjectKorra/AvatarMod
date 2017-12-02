package com.crowsofwar.avatar.common.analytics;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.util.LinkedList;
import java.util.Queue;

public class EventAnalytics {

	public static final String GA_TRACKING_ID = "UA-110529537-1";
	public static final String GA_CLIENT_ID = "1";

	/**
	 * Maximum amount of events that can be sent in bulk
	 */
	public static final int MAX_BULK_EVENTS = 20;

	private final Queue<AnalyticEvent> queuedEvents = new LinkedList<>();

	/**
	 * Adds the given events to the queue to be sent later.
	 */
	public void pushEvent(AnalyticEvent event) {
		queuedEvents.add(event);
	}

	/**
	 * Adds the given events to the queue to be sent later.
	 */
	public void pushEvents(AnalyticEvent... events) {
		for (AnalyticEvent event : events) {
			queuedEvents.add(event);
		}
	}

	/**
	 * Sends all currently queued events to the server
	 */
	public void uploadEvents() {
		AnalyticEvent[] queuedEventsArray = queuedEvents.toArray(new AnalyticEvent[0]);
		sendEvents(queuedEventsArray);
	}

	/**
	 * Gets the appropriate parameters to use when sending the event to google
	 */
	private String getEventParameters(AnalyticEvent event) {
		String params = "v=1";
		params += "&tid=" + GA_TRACKING_ID;
		params += "&cid=" + GA_CLIENT_ID;
		params += "&t=event";
		params += "&ec=" + event.getCategory();
		params += "&ea=" + event.getAction();
		return params;
	}

	/**
	 * Sends the given events to the server through an HTTP post request.
	 */
	private void sendEvents(AnalyticEvent... events) {

		// Must only allow up to MAX_BULK_EVENTS in each POST request, which results in this
		// algorithm
		// In each "round", i.e. execution of while loop, one batch request is sent, containing
		// several of the events, up to MAX_BULK_EVENTS.
		// Keep track of
		// 1) Total events sent so far (sentEvents); needed to keep track of overall which events
		//    still need sending
		// 2) Events sent in this round (sentHere); lets us know when to end the round (once
		//    MAX_BULK_EVENTS has been reached)

		int sentEvents = 0;

		while (events.length - sentEvents > 0) {

			String params = "";
			int sentHere = 0;
			for (; sentEvents < events.length && sentHere < MAX_BULK_EVENTS; sentHere++) {

				params += getEventParameters(events[sentEvents]) + "\n";
				sentEvents++;

			}

			// In case there's only 1 being sent, just use regular endpoint
			String url = sentHere == 1 ? "https://www.google-analytics.com/collect"
					: "https://www.google-analytics.com/batch";

			post(url, params);

		}


	}

	private void post(String url, String payload) {

		// https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java

		try {

			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			post.setEntity(new StringEntity(payload, ContentType.create("text/plain", "UTF-8")));

			client.execute(post);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
