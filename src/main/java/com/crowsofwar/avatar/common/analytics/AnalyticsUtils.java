package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.common.config.ConfigAnalytics;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

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
		if (ConfigAnalytics.ANALYTICS_CONFIG.analyticsEnabled) {
			post("https://www.google-analytics.com/collect", params);
		}
	}

	/**
	 * Makes a request to the GA API
	 */
	public static void makeBatchApiRequest(String params) {
		if (ConfigAnalytics.ANALYTICS_CONFIG.analyticsEnabled) {
			post("https://www.google-analytics.com/batch", params);
		}
	}

	public static void post(String url, String payload) {

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
