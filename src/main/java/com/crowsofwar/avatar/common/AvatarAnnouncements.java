package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages public announcements for Avatar Mod 2, which are short messages which can be sent by the
 * developers online notifying players about important information.
 *
 * @author CrowsOfWar
 */
public class AvatarAnnouncements {

	public static final String ANNOUNCEMENT_URL = "https://pastebin.com/raw/Mm8grAfe";

	private static List<String> announcements;

	public static void fetchAnnouncements() {

		AvatarLog.info("Fetching latest AvatarMod2 announcements...");

		try {

			announcements = new ArrayList<>();

			URL u = new URL(ANNOUNCEMENT_URL);
			URLConnection conn = u.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				announcements.add(inputLine);
			}

			in.close();

		} catch (Exception ex) {

			AvatarLog.error("Your game will be fine, but there was a problem getting Av2 announcements", ex);
			return;

		}

		AvatarLog.info("Finished loading announcements");

	}

}
