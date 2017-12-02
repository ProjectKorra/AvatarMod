package com.crowsofwar.avatar.common.analytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AvatarAnalytics {

	private static final Queue<AnalyticEvent> events = new LinkedList<>();

	public static void pushEvent(AnalyticEvent event) {
		events.add(event);
	}

	public static void sendEvents() {
		while (!events.isEmpty()) {
			sendEvent(events.poll());
		}
	}

	public static void main(String[] args) {



	}

	private void sendEvent(AnalyticEvent event) {
		Map<String, String> params = new HashMap<>();
		params.put("v", "1");
		params.put("tid", "UA-110529537-1");
		params.put("cid", "2");
		params.put("t", "event");
		params.put("ec", "test_desktop");
		params.put("ea", "test1");
		post("https://www.google-analytics.com/collect", params);
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
