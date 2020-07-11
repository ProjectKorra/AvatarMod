package com.crowsofwar.avatar.network;

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

	public static final String ANNOUNCEMENT_URL = "https://raw.githubusercontent.com/CrowsOfWar/AvatarMod-Announcements/master/announcements.txt";

	private static List<Announcement> announcements;
	
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {

		for (Announcement announcement : announcements) {
			long hoursTotal = announcement.getHoursAgo();
			if (hoursTotal > 24) {
				long days = hoursTotal / 24;
				if (days == 1) {
					AvatarChatMessages.MSG_ANNOUNCEMENT_YESTERDAY.send(e.player, announcement.contents, days);
				} else {
					AvatarChatMessages.MSG_ANNOUNCEMENT_DAYS.send(e.player, announcement.contents, days);
				}
			} else {
				AvatarChatMessages.MSG_ANNOUNCEMENT_TODAY.send(e.player, announcement.contents);
			}

		}

	}

	/**
	 * Cleans up the announcements so that they are ready for use. Sorts them so most recent announcements come first,
	 * and removes any announcements later than 72 hours ago.
	 */
	private static void cleanupAnnouncements() {

		announcements.sort(Comparator.comparingLong(Announcement::getHoursAgo));
		announcements.removeIf(announcement -> announcement.getHoursAgo() > 72);

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

			cleanupAnnouncements();

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

		public long getHoursAgo() {
			long msDiff = (System.currentTimeMillis() - timestamp.getTime());
			return msDiff / 1000 / 60 / 60;
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
