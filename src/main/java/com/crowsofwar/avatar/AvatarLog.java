package com.crowsofwar.avatar;

public class AvatarLog {
	
	public static void debug(String s) {
		if (AvatarInfo.IS_DEV_BUILD)
			System.out.println("[Debug] " + s);
	}
	
	public static void info(String s) {
		System.out.println("[Info] " + s);
	}
	
	public static void error(String s) {
		System.err.println("[Error] " + s);
	}
	
	public static void warn(String s) {
		System.err.println("[Warn] " + s);
	}
	
}
