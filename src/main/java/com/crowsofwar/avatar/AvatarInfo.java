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
 * Using semantic versioning. This isn't an API, but I'm not too lazy to be
 * backwards-compatible. Everything is automatically calculated
 * <p>
 * Versioning scheme: {RELEASE}.{UPDATE}.{PATCH}{DEV_STAGE}
 * </p>
 *
 * <p>
 * DEV_STAGE is alpha ("-alpha"), beta ("-beta"), or full release ("").
 * Append "-dev" if a development build
 * </p>
 *
 * <p>
 * E.g. 1.3.2-beta-dev -> Release 1, Update 3, patch 2, Beta, development build
 * </p>
 *
 * @author CrowsOfWar
 * @author Mahtaran
 */
public class AvatarInfo {
	public enum DevelopmentState {
		ALPHA,
		BETA,
		RELEASE;
	}

	// Everything is not adjustable / automatically calculated
	
	public static final String VERSION = "@VERSION@";
	/**
	 * Incremented for complete rewrites
	 */
	public static final int VERSION_RELEASE;
	/**
	 * Incremented for every major update.
	 */
	public static final int VERSION_UPDATE;
	/**
	 * Incremented for minor bug fixes.
	 */
	public static final int VERSION_PATCH;

	/**
	 * Current development state
	 */
	public static final DevelopmentState DEV_STAGE;
	
	
	public static final boolean IS_DEVELOPMENT;
	
	static {
		String[] appends = VERSION.split("-");
		String[] versions = appends[0].split("\\.");
		VERSION_RELEASE = Integer.parseInt(versions[0]);
		VERSION_RELEASE = Integer.parseInt(versions[1]);
		VERSION_RELEASE = Integer.parseInt(versions[2]);
		if (appends.length > 1) {
			for (int i = 1; i < appends.length; i++) {
				String appendix = appends[i];
				if (appendix.contentEquals("alpha")) DEV_STAGE = DevelopmentState.ALPHA;
				else if (appendix.contentEquals("beta")) DEV_STAGE = DevelopmentState.BETA;
				if (DEV_STAGE != null) break;
			}
		}
		if (DEV_STAGE == null) DEV_STAGE = DevelopmentState.RELEASE;
		IS_DEVELOPMENT = VERSION.contains("-dev");
	}

	public static final String MOD_ID = "avatarmod";
	public static final String MOD_NAME = "Avatar Mod: Out of the Iceberg";
	public static final String MC_VERSION = "1.12.2";
}
