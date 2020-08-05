package com.crowsofwar.avatar.common.util;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A class representing a semantic version. It is specified as the following:
 * major.minor.patch-state.pre (1) OR major.minor.patch-pre.pre (2). State is alpha or beta, major
 * is the (API) version, minor is the feature version, and patch is the bugfix version. pre is the
 * pre-release version. If the version state is full, then the second (2) version template will be
 * used.
 *
 * @author Mahtaran
 */
public class SemanticVersion implements Comparable<SemanticVersion> {
	private final EnumVersionState versionState;
	private final int majorVersion;
	private final int minorVersion;
	private final int patchVersion;
	private final int preReleaseVersion;
	private final String versionString;// Since this object is immutable, the version string doesn't

	public SemanticVersion(EnumVersionState versionState, int majorVersion, int minorVersion,
	                       int patchVersion,
	                       int preReleaseVersion) {
		Objects.requireNonNull(versionState);
		if (majorVersion < 0)
			throw new IllegalArgumentException("Major version is less then zero");
		if (minorVersion < 0)
			throw new IllegalArgumentException("Minor version is less then zero");
		if (patchVersion < 0)
			throw new IllegalArgumentException("Patch version is less then zero");
		if (preReleaseVersion < -1) {
			preReleaseVersion = -1;
		}
		this.versionState = versionState;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
		this.preReleaseVersion = preReleaseVersion;
		versionString = buildVersionString();
	}
	// have to be computed every call of toString

	public SemanticVersion(EnumVersionState versionState, int majorVersion, int minorVersion,
	                       int patchVersion) {
		this(versionState, majorVersion, minorVersion, patchVersion, -1);
	}

	/**
	 * Parses the specified version string and returns a semantic version. Throws an {@link
	 * IllegalArgumentException} if the version string is invalid.
	 *
	 * @param versionString The version string
	 * @return The semantic version
	 */
	public static SemanticVersion of(String versionString) {
		Objects.requireNonNull(versionString);
		String trimmedVersion = versionString.trim();
		if (!Pattern.matches(
						"^(?i)[0-9]+\\.[0-9]+\\.[0-9]+($|(-PRE)|(-ALPHA)|(-BETA))($|(\\.[0-9]+))$",
						trimmedVersion))
			throw new IllegalArgumentException(
							String.format("The version string \"%s\" doesn't match the specification",
							              trimmedVersion));
		String[] parts = trimmedVersion.split("-");
		String[] mainVersion = parts[0].split("\\.");
		int majorVersion = Integer.parseInt(mainVersion[0]);
		int minorVersion = Integer.parseInt(mainVersion[1]);
		int patchVersion = Integer.parseInt(mainVersion[2]);
		EnumVersionState versionState = EnumVersionState.FULL;
		int preReleaseVersion = -1;
		if (parts.length > 1) {
			String[] parts2 = parts[1].split("\\.");
			String state = parts2[0].replaceAll("-", "");
			if (!state.equals("PRE")) {
				versionState = EnumVersionState.valueOf(state.toUpperCase());
			}
			if (parts2.length > 1) {
				preReleaseVersion = Integer.parseInt(parts2[1]);
			}
		}
		return new SemanticVersion(versionState, majorVersion, minorVersion, patchVersion,
		                           preReleaseVersion);
	}

	public EnumVersionState getVersionState() {
		return versionState;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getPatchVersion() {
		return patchVersion;
	}

	public int getPreReleaseVersion() {
		return preReleaseVersion;
	}

	public boolean isPreRelease() {
		return preReleaseVersion >= 0;
	}

	@Override
	public int compareTo(SemanticVersion comparable) {
		int stateComp = versionState.compareTo(comparable.versionState);
		int majorComp = Integer.compare(majorVersion, comparable.majorVersion);
		int minorComp = Integer.compare(minorVersion, comparable.minorVersion);
		int patchComp = Integer.compare(patchVersion, comparable.patchVersion);
		int preReleaseComp = Integer.compare(preReleaseVersion, comparable.preReleaseVersion);
		if (preReleaseVersion < 0 || comparable.preReleaseVersion < 0) {
			preReleaseComp = -preReleaseComp;
		}
		return stateComp == 0
		       ? majorComp == 0 ? minorComp == 0
		                          ? patchComp == 0 ? preReleaseComp : patchComp
		                          : minorComp : majorComp
		       : stateComp;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + majorVersion;
		result = prime * result + minorVersion;
		result = prime * result + patchVersion;
		result = prime * result + preReleaseVersion;
		result = prime * result + (versionState == null ? 0 : versionState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SemanticVersion other = (SemanticVersion) obj;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		if (patchVersion != other.patchVersion)
			return false;
		if (preReleaseVersion != other.preReleaseVersion)
			return false;
		return versionState == other.versionState;
	}

	private String buildVersionString() {
		return String.format("%d.%d.%d%s%s", majorVersion, minorVersion,
		                     patchVersion,
		                     versionState == EnumVersionState.FULL
		                     ? ""
		                     : String.format("-%s", versionState.toString().toLowerCase()),
		                     preReleaseVersion >= 0
		                     ? String.format("%s.%d", versionState == EnumVersionState.FULL
		                                              ? "-pre"
		                                              : "",
		                                     preReleaseVersion)
		                     : "");
	}

	@Override
	public String toString() {
		return versionString;
	}

	public enum EnumVersionState {
		ALPHA, BETA, FULL
	}

}
