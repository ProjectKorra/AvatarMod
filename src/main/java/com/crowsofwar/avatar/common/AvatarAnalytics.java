package com.crowsofwar.avatar.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AvatarAnalytics {

	private static void post(String url, Map<String, String> params) {

		// https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java

		try {

			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

			// Request parameters and other properties.
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			Set<Map.Entry<String, String>> entries = params.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

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
