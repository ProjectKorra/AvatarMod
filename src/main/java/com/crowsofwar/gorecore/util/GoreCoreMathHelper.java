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

import java.util.Random;

/**
 * Provides a variety of numerical functions related to math and random numbers.
 * 
 * @author CrowsOfWar
 */
public final class GoreCoreMathHelper {
	
	/**
	 * Clamp the double to the given range.
	 * 
	 * @param n
	 *            The number to clamp
	 * @param min
	 *            The minimum number in that range
	 * @param max
	 *            The maximum number in that range
	 * @return The clamped number
	 */
	public static double clampDouble(double n, double min, double max) {
		if (n < min) n = min;
		if (n > max) n = max;
		return n;
	}
	
	/**
	 * Round the given number to the nearest whole.
	 * 
	 * @param n
	 *            The number
	 * @return The number, rounded to the nearest integer
	 */
	public static int round(double n) {
		return (int) (n + 0.5);
	}
	
	/**
	 * Round the given number to the nearest whole that is smaller than the number.
	 * 
	 * @param n
	 *            The number
	 * @return The number, rounded to the nearest smaller integer
	 */
	public static int roundDown(double n) {
		return (int) n;
	}
	
	/**
	 * Round the given number to the nearest whole that is greater than the number.
	 * 
	 * @param n
	 *            The number
	 * @return The number, rounded to the nearest greater integer
	 */
	public static int roundUp(double n) {
		return roundDown(n) + 1;
	}
	
	public static final Random random = new Random();
	
	/**
	 * Generate a random boolean.
	 */
	public static boolean randomBoolean() {
		return random.nextBoolean();
	}
	
	/**
	 * Generate a double in the given range.
	 * 
	 * @param min
	 *            The minimum number to generate
	 * @param max
	 *            The maximum number to generate
	 * @return A random double in the given range
	 */
	public static double randomDouble(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}
	
	/**
	 * Generate a float in the given range.
	 * 
	 * @param min
	 *            The minimum number to generate
	 * @param max
	 *            The maximum number to generate
	 * @return A random float in the given range
	 */
	public static float randomFloat(float min, float max) {
		return min + random.nextFloat() * (max - min);
	}
	
	/**
	 * Generate an integer in the given range.
	 * 
	 * @param min
	 *            The minimum number to generate
	 * @param max
	 *            The maximum number to generate
	 * @return A random integer in the given range
	 */
	public static int randomInt(int min, int max) {
		return min + random.nextInt(max - min);
	}
	
	/**
	 * Generate a long in the given range.
	 * 
	 * @param min
	 *            The minimum number to generate
	 * @param max
	 *            The maximum number to generate
	 * @return A random long in the given range
	 */
	public static long randomLong(long min, long max) {
		return min + random.nextLong() * (max - min);
	}
	
}
