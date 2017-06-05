/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar;

import org.apache.logging.log4j.Logger;

public class AvatarLog {
	
	static Logger log;
	
	public static void debug(String s) {
		if (AvatarInfo.IS_DEVELOPMENT) log.debug("[Debug] " + s);
	}
	
	public static void info(String s) {
		log.info("[Info] " + s);
	}
	
	public static void error(String s) {
		log.error("[Error] " + s);
	}
	
	public static void error(String s, Throwable t) {
		log.error("[Error] " + s, t);
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
	
	public static void warn(WarningType type, String s, Throwable t) {
		log.warn("[Warn/" + type + "]" + s, t);
		if (type == WarningType.INVALID_CODE) {
			Thread.dumpStack();
		}
	}
	
	/**
	 * Output a warning to the log that the player might have been hacking
	 */
	// TODO Notify the admins
	public static void warnHacking(String username, String s) {
		warn(WarningType.BAD_CLIENT_PACKET, "Player " + username + ": Unexpected data, " + s);
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
		BAD_CLIENT_PACKET,
		/**
		 * Server sent abnormal input which is not 'correct' on client
		 */
		WEIRD_PACKET,
		/**
		 * Miswritten configuration files
		 */
		CONFIGURATION;
	}
	
}
