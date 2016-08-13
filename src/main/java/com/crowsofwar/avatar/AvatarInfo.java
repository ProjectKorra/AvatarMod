package com.crowsofwar.avatar;

/**
 * Not using semantic versioning. This isn't an API, and I'm too lazy to be backwards-compatible.
 * <p>
 * Versioning scheme: MC-{MCVERSION}-{UPDATE}.{PATCH}{DEV_STAGE}{DEV_BUILD}
 * <p>
 * DEV_STAGE is for alpha("a"), beta("b"), or full release("").
 * <p>
 * If development version, DEV_BUILD is "(dev)"
 * 
 * @author CrowsOfWar
 */
public class AvatarInfo {
	
	public static final String MOD_ID = "avatarmod";
	public static final String MOD_NAME = "Avatar Mod: Bringing Bending to Minecraft Players";
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
	 * "a" for alpha.
	 * <p>
	 * "b" for beta.
	 * <p>
	 * "" for full release.
	 */
	public static final String DEV_STAGE = "a";
	public static final String MC_VERSION = "1.7.10";
	public static final String VERSION = "MC" + MC_VERSION + "-AV" + VERSION_UPDATE + "." + VERSION_PATCH + DEV_STAGE
			+ (IS_DEV_BUILD ? "(dev)" : "");
	
}
