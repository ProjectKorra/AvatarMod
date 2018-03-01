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
 * Versioning scheme: AV{DEV_STAGE}{UPDATE}.{PATCH}{DEV_BUILD} * <p>
 * DEV_STAGE is for alpha("a"), beta("b"), or full release("").
 * <p>
 * If development version, DEV_BUILD is "_dev"
 * <p>
 * E.g. AV_B3.2-dev -> Beta, Update 3, patch 2, development build
 *
 * @author CrowsOfWar
 */
public class AvatarInfo {

	// Things that are adjustable

	/**
	 * Incremented for every major update.
	 */
	public static final int VERSION_UPDATE = 5;
	/**
	 * Incremented for minor bug fixes.
	 */
	public static final int VERSION_PATCH = 3;
	/**
	 * "a" for alpha.
	 * <p>
	 * "b" for beta.
	 * <p>
	 * "" for full release.
	 */
	public static final String DEV_STAGE = "a";
	public static final String MOD_ID = "avatarmod";

	// Not adjustable / automatically calculated
	public static final String MOD_NAME = "Avatar Mod: Out of the Iceberg";
	public static final String MC_VERSION = "1.12.2";
	/**
	 * Type of version; 0 for production; 1 for development; 2 for preview 1; 3
	 * for preview 2, etc
	 * <p>
	 * Accessed via {@link #IS_PRODUCTION}, {@link #IS_PREVIEW},
	 * {@link #IS_DEVELOPMENT}
	 */
	private static final int VERSION_TYPE = 0;
	public static final boolean IS_PRODUCTION = VERSION_TYPE == 0;
	public static final boolean IS_DEVELOPMENT = VERSION_TYPE == 1;
	public static final boolean IS_PREVIEW = VERSION_TYPE >= 2;
	public static final String VERSION = IS_PRODUCTION ? PRODUCTION_VERSION : DEV_STAGE + VERSION_UPDATE + "." + VERSION_PATCH
			+ (IS_PREVIEW ? "_preview" + (VERSION_TYPE - 1) : "_dev");
	/**
	 * Automatically updated when the mod is build
	 */
	public static final String PRODUCTION_VERSION = "@VERSION@";

	public enum VersionType {

		PRODUCTION,
		PREVIEW,
		DEVELOPMENT

	}

}
