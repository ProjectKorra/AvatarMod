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

package com.crowsofwar.gorecore.util;

/**
 * A utility class for parsing generic data types.
 * 
 * @author CrowsOfWar
 * @see GoreCoreParsingResult
 */
public class GoreCoreParsingUtil {
	
	/**
	 * Parse the string into an integer.
	 * 
	 * @param str
	 *            The string to parse
	 * @return The result of parsing the string to an integer
	 * @see Integer#parseInt(String)
	 */
	public static GoreCoreParsingResult.ResultInteger parseInt(String str) {
		try {
			return GoreCoreParsingResult.generateIntegerResult(Integer.parseInt(str), true);
		} catch (NumberFormatException e) {
			return GoreCoreParsingResult.generateIntegerResult(0, false);
		}
	}
	
	/**
	 * Parse the string into a float.
	 * 
	 * @param str
	 *            The string to parse
	 * @return The result of parsing the string to a float
	 * @see Float#parseFloat(String)
	 */
	public static GoreCoreParsingResult.ResultFloat parseFloat(String str) {
		try {
			return GoreCoreParsingResult.generateFloatResult(Float.parseFloat(str), true);
		} catch (NumberFormatException e) {
			return GoreCoreParsingResult.generateFloatResult(0, false);
		}
	}
	
	/**
	 * Parse the string into a double.
	 * 
	 * @param str
	 *            The string to parse
	 * @return The result of parsing the string to a double
	 * @see Double#parseDouble(String)
	 */
	public static GoreCoreParsingResult.ResultDouble parseDouble(String str) {
		try {
			return GoreCoreParsingResult.generateDoubleResult(Double.parseDouble(str), true);
		} catch (NumberFormatException e) {
			return GoreCoreParsingResult.generateDoubleResult(0, false);
		}
	}
	
	/**
	 * Parse the string into a long.
	 * 
	 * @param str
	 *            The string to parse
	 * @return The result of parsing the string to a long
	 * @see Long#parseLong(String)
	 */
	public static GoreCoreParsingResult.ResultLong parseLong(String str) {
		try {
			return GoreCoreParsingResult.generateLongResult(Long.parseLong(str), true);
		} catch (NumberFormatException e) {
			return GoreCoreParsingResult.generateLongResult(0, false);
		}
	}
	
	/**
	 * Parse the string into a boolean.
	 * 
	 * @param str
	 *            The string to parse
	 * @return The result of parsing the string to a boolean
	 * @see Boolean#parseBoolean(String)
	 */
	public static GoreCoreParsingResult.ResultBoolean parseBoolean(String str) {
		return GoreCoreParsingResult.generateBooleanResult(Boolean.parseBoolean(str), true);
	}
	
}
