package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Manages public announcements for Avatar Mod 2, which are short messages which can be sent by the
 * developers online notifying players about important information.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarAnnouncements {

	public static final String ANNOUNCEMENT_URL = "https://pastebin.com/raw/x1Z3sXp6";

	private static List<Announcement> announcements;

	public static void main(String[] args) {
		Announcement announcement = new Announcement("2018-01-08 Here is the first announcement");
		System.out.println(announcement);

		Date start = announcement.timestamp;
		Date end = new Date();
		long diffInSeconds = (end.getTime() - start.getTime()) / 1000;
		long diffInHours = diffInSeconds / 60 / 60;

	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {

		AvatarChatMessages.MSG_ANNOUNCEMENT.send(e.player, announcements.get(0));

	}

	public static void fetchAnnouncements() {

		AvatarLog.info("Fetching latest AvatarMod2 announcements...");
		announcements = new ArrayList<>();

		try {

			URL u = new URL(ANNOUNCEMENT_URL);
			URLConnection conn = u.openConnection();
			conn.setConnectTimeout(3000);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				// Interpret and add announcement
				try {

					announcements.add(new Announcement(inputLine));

				} catch (IllegalArgumentException ex) {
					// Incorrect format string from announcement
					AvatarLog.error("Developers sent an incorrectly formatted announcement", ex);
				}

			}

			in.close();

		} catch (Exception ex) {

			AvatarLog.error("Your game will be fine, but there was a problem getting Av2 announcements", ex);
			return;

		}

		AvatarLog.info("Finished loading announcements");

	}

	private static class Announcement {

		private final String contents;
		private final Date timestamp;

		/**
		 * Constructs an announcement from the code string. The code should be in the following
		 * format:
		 * <p>
		 * YYYY-MM-DD
		 */
		public Announcement(String code) {

			if (!code.matches("\\d{4}-\\d{2}-\\d{2} .+")) {
				throw new IllegalArgumentException("Announcement code in incorrect format: " + code);
			}

			String[] timeStrings = code.split(" ")[0].split("-");
			int year = Integer.parseInt(timeStrings[0]);
			int month = Integer.parseInt(timeStrings[1]) - 1;
			int day = Integer.parseInt(timeStrings[2]);

			Calendar cal = new GregorianCalendar(year, month, day);
			cal.setTimeZone(getSystemTimeZone());
			timestamp = cal.getTime();

			contents = code.substring(code.indexOf(" ") + 1);

		}

		public Announcement(String contents, Date timestamp) {
			this.contents = contents;
			this.timestamp = timestamp;
		}

		@Override
		public String toString() {
			return "[" + timestamp + "] " + contents;
		}
	}

	/**
	 * Gets the timezone which the user has configured the system to use. If unavailable, just uses
	 * UTC time.
	 */
	private static TimeZone getSystemTimeZone() {

		String timezone = System.getProperty("user.timezone");
		if (timezone != null) {
			return TimeZone.getTimeZone(timezone);
		} else {
			return TimeZone.getTimeZone("UTC");
		}

	}

}
