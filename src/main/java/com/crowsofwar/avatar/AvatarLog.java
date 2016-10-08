package com.crowsofwar.avatar;

public class AvatarLog {
	
	public static void debug(String s) {
		if (AvatarInfo.IS_DEV_BUILD) System.out.println("[Debug] " + s);
	}
	
	public static void info(String s) {
		System.out.println("[Info] " + s);
	}
	
	public static void error(String s) {
		System.err.println("[Error] " + s);
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
		System.err.println("[Warn/" + type + "] " + s);
	}
	
	public enum WarningType {
		UNKNOWN,
		INVALID_SAVE,
		POSSIBLE_HACKING;
	}
	
}
