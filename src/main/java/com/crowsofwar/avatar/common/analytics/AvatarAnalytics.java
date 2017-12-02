package com.crowsofwar.avatar.common.analytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class AvatarAnalytics {

	public static final String GA_TRACKING_ID = "UA-110529537-1";
	public static final String GA_CLIENT_ID = "1";

	/**
	 * Maximum amount of events that can be sent in bulk
	 */
	public static final int MAX_BULK_EVENTS = 20;

	private static final Queue<AnalyticEvent> queuedEvents = new LinkedList<>();

	public static void pushEvent(AnalyticEvent event) {
		queuedEvents.add(event);
	}

	public static void pushEvents(AnalyticEvent... events) {
		for (AnalyticEvent event : events) {
			queuedEvents.add(event);
		}
	}

	public static void sendEvents() {
		while (!queuedEvents.isEmpty()) {
			sendEvent(queuedEvents.poll());
		}
	}

	public static void main(String[] args) {


		AnalyticEvent[] events = new AnalyticEvent[30];
		Arrays.fill(events, AnalyticEvent.TEST_1);
		sendEvents(events);
//		pushEvents(AnalyticEvent.TEST_1, AnalyticEvent.TEST_2);

//		post("https://www.google-analytics.com/batch", "v=1&t=event&tid=UA-110529537-1&cid=3&ec=category&ea=actionmoviehero", "v=1&t=event&tid=UA-110529537-1&cid=3&ec=category&ea=actionmoviehero2");

	}

	private static String getEventParameters(AnalyticEvent event) {
		String params = "v=1";
		params += "&tid=" + GA_TRACKING_ID;
		params += "&cid=" + GA_CLIENT_ID;
		params += "&t=event";
		params += "&ec=" + event.getCategory();
		params += "&ea=" + event.getAction();
		return params;
	}

	private static void sendEvent(AnalyticEvent event) {
		post("https://www.google-analytics.com/collect", getEventParameters(event));
	}

	/**
	 * @return the amount of events that were unsent
	 */
	private static void sendEvents(AnalyticEvent... events) {

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

	private static void post(String url, String... payloads) {

		// https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java

		try {

			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);

			String contents = "";
			for (String payload : payloads) {
				contents += payload + "\n";
			}

			httppost.setEntity(new StringEntity(contents, ContentType.create("text/plain", "UTF-8")));

			//Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					// do something useful
				} finally {
					instream.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
