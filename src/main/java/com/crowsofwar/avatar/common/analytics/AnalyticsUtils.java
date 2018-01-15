package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.common.config.ConfigAnalytics;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author CrowsOfWar
 */
public class AnalyticsUtils {

	public static final String GA_TRACKING_ID = "UA-110529537-1";

	/**
	 * Gets the basic parameters to be sent in any GA API request. Then other parameters can be
	 * added like type, event category, etc. depending on the purpose.
	 */
	public static String getBasicParameters() {
		String params = "v=1";
		params += "&tid=" + GA_TRACKING_ID;
		params += "&cid=" + ConfigAnalytics.ANALYTICS_CONFIG.analyticsId;
		return params;
	}

	/**
	 * Makes a request to the GA API
	 */
	public static void makeSingleApiRequest(String params) {
		if (ConfigAnalytics.ANALYTICS_CONFIG.isAnalyticsEnabled()) {
			post("https://www.google-analytics.com/collect", params);
		}
	}

	/**
	 * Makes a request to the GA API
	 */
	public static void makeBatchApiRequest(String params) {
		if (ConfigAnalytics.ANALYTICS_CONFIG.isAnalyticsEnabled()) {
			post("https://www.google-analytics.com/batch", params);
		}
	}

	public static void post(String url, String payload) {

		// We can't use Apache HTTP libraries here because for some reason they aren't loaded on the
		// server
		// https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java

		HttpURLConnection connection = null;
		try {

			URL urlObject = new URL(url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

			connection.setFixedLengthStreamingMode(payloadBytes.length);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(payloadBytes);
			outputStream.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (connection != null) {
				connection.disconnect();
			}

		}

	}

}
