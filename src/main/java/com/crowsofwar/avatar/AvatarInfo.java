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
 * Using semantic versioning. (Well, kind of) This isn't an API, but I'm not too lazy to be
 * backwards-compatible.
 * <p>
 * Versioning scheme: {REWRITE}.{UPDATE}.{PATCH}-{DEV_STAGE}-{APPENDIX}
 * </p>
 * <p>
 * REWRITE is increased with every rewrite of the mod (AKA never), UPDATE is increased with every update of the mod, and PATCH is (Who would've guessed it) increased with every patch
 * </p>
 * <p>
 * DEV_STAGE is for alpha ("alpha"), beta ("beta"), or full release ("").
 * </p>
 * <p>
 * If development version, APPENDIX is "dev"
 * </p>
 * <p>
 * E.g. 1.3.2-beta-dev -> Rewrite 1, Update 3, Patch 2, Beta, Development build
 * </p>
 *
 * @author Mahtaran
 */
public class AvatarInfo {
	public static final String MODID = "@modid@";
	public static final String MOD_NAME = "@name@";
	public static final String MC_VERSION = "@mcversion@";
	public static final String VERSION = "@version@";
	public static final boolean IS_DEVELOPMENT = true;
	public static final boolean IS_PREVIEW = true;
}
