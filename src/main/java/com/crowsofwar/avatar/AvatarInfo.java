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

/**
 * Not using semantic versioning. This isn't an API, and I'm too lazy to be
 * backwards-compatible.
 * <p>
 * Versioning scheme: AV{DEV_STAGE}{UPDATE}.{PATCH}{DEV_BUILD}
 * <p>
 * DEV_STAGE is for alpha("a"), beta("b"), or full release("").
 * <p>
 * If development version, DEV_BUILD is "_dev"
 * <p>
 * E.g. AV_B3.2-dev -> Beta, Update 3, patch 2, development build
 * 
 * @author CrowsOfWar
 */
public class AvatarInfo {
	
	public static final String MOD_ID = "avatarmod";
	public static final String MOD_NAME = "Avatar Mod: Out of the Iceberg";
	/**
	 * True if development update.
	 */
	public static final boolean IS_DEV_BUILD = false;
	/**
	 * Incremented for every major update.
	 */
	public static final int VERSION_UPDATE = 1;
	/**
	 * Incremented for minor bug fixes.
	 */
	public static final int VERSION_PATCH = 2;
	/**
	 * "A" for alpha.
	 * <p>
	 * "B" for beta.
	 * <p>
	 * "" for full release.
	 */
	public static final String DEV_STAGE = "A";
	public static final String MC_VERSION = "1.10.2";
	public static final String VERSION = "AV_" + DEV_STAGE + VERSION_UPDATE + "." + VERSION_PATCH
			+ (IS_DEV_BUILD ? "_dev" : "");
	
}
