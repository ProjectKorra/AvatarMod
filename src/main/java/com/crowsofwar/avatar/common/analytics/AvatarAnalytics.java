package com.crowsofwar.avatar.common.analytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class AvatarAnalytics {

	public static final String GA_TRACKING_ID = "UA-110529537-1";
	public static final String GA_CLIENT_ID = "1";

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

	private static void sendEvents(AnalyticEvent... events) {
		String params = "";
		for (AnalyticEvent event : events) {
			params += getEventParameters(event) + "\n";
		}
		post("https://www.google-analytics.com/batch", params);
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
