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
	public static final int VERSION_PATCH = 0;
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
