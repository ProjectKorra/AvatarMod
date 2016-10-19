package com.crowsofwar.avatar;

import org.apache.logging.log4j.Logger;

public class AvatarLog {
	
	static Logger log;
	
	public static void debug(String s) {
		if (AvatarInfo.IS_DEV_BUILD) log.debug("[Debug] " + s);
	}
	
	public static void info(String s) {
		log.info("[Info] " + s);
	}
	
	public static void error(String s) {
		log.error("[Error] " + s);
	}
	
	/**
	 * @deprecated Use {@link #warn(WarningType, String)}.
	 */
	@Deprecated
	public static void warn(String s) {
		warn(WarningType.UNKNOWN, s);
	}
	
	/**
	 * Output a warning with the given category.
	 * 
	 * @param type
	 *            Type of warning
	 * @param s
	 *            String to print
	 */
	public static void warn(WarningType type, String s) {
		log.warn("[Warn/" + type + "] " + s);
		if (type == WarningType.INVALID_CODE) {
			Thread.dumpStack();
		}
	}
	
	/**
	 * Output a warning to the log that the player might have been hacking
	 */
	// TODO Notify the admins
	public static void warnHacking(String username, String s) {
		warn(WarningType.POSSIBLE_HACKING, "Player " + username + ": Unexpected data, " + s);
	}
	
	public enum WarningType {
		/**
		 * No warning type was specified
		 */
		UNKNOWN,
		/**
		 * Invalid data has been saved
		 */
		INVALID_SAVE,
		/**
		 * Invalid values for a method have been specified
		 */
		INVALID_CODE,
		/**
		 * A client sent abnormal input which might try to exploit glitches
		 */
		POSSIBLE_HACKING,
		/**
		 * Server sent abnormal input which is not 'correct' on client
		 */
		WEIRD_PACKET;
	}
	
}
