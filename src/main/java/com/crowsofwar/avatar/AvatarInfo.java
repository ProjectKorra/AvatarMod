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

import java.util.regex.Pattern;

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
	public enum DevelopmentStage {
		ALPHA,
		BETA,
		RELEASE;
	}

	// Everything is not adjustable / automatically calculated
	
	public static final String VERSION = @VERSION@;
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
	 * Current development stage
	 */
	public static final DevelopmentStage DEV_STAGE;
	
	/*
	 * Patterns used to find out if the version string contains certain suffixes
	 */
	private static final Pattern ALPHA = Pattern.compile(Pattern.quote("-alpha"), Pattern.CASE_INSENSITIVE);
	private static final Pattern BETA = Pattern.compile(Pattern.quote("-beta"), Pattern.CASE_INSENSITIVE);
	private static final Pattern DEV = Pattern.compile(Pattern.quote("-dev"), Pattern.CASE_INSENSITIVE);
	
	/**
	 * true if this is a development version
	 */
	public static final boolean IS_DEVELOPMENT;
	
	static {
		String[] versions = VERSION.split("-")[0].split("\\.");
		VERSION_RELEASE = Integer.parseInt(versions[0]);
		VERSION_UPDATE = Integer.parseInt(versions[1]);
		VERSION_PATCH = Integer.parseInt(versions[2]);
		if (ALPHA.matcher(VERSION).find()) DEV_STAGE = DevelopmentStage.ALPHA;
		else if (BETA.matcher(VERSION).find()) DEV_STAGE = DevelopmentStage.BETA;
		else DEV_STAGE = DevelopmentStage.RELEASE;
		IS_DEVELOPMENT = DEV.matcher(VERSION).find();
	}

	public static final String MOD_ID = "avatarmod";
	public static final String MOD_NAME = "Avatar Mod: Out of the Iceberg";
	public static final String MC_VERSION = "1.12.2";
	public static final String UPDATE_JSON = "http://avatar.amuzil.com/updates.json";
}
