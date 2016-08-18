package com.crowsofwar.gorecore.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraftforge.fml.common.FMLLog;

/**
 * <p>
 * Checks for the latest version based off of an online text file.
 * </p>
 * 
 * <p>
 * Notifying the players about the version is up to the class extending the version checker.
 * </p>
 * 
 * @author CrowsOfWar
 */
public abstract class GoreCoreVersionChecker {
	
	/**
	 * The version of the mod currently installed
	 */
	private final String currentVersion;
	
	/**
	 * The latest version of the mod, fetched online
	 */
	private final String latestVersion;
	
	public GoreCoreVersionChecker(String currentVersion, String url) {
		// Because eclipse was giving me annoying warnings T_T
		String latest = currentVersion;
		
		try {
			
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			latest = br.readLine();
			
			br.close();
			
		} catch (Exception e) {
			FMLLog.warning("GoreCore> Could not load latest version at URL " + url);
			e.printStackTrace();
		}
		
		this.currentVersion = currentVersion;
		this.latestVersion = latest;
		
	}
	
	/**
	 * Get the version currently installed
	 */
	public String currentVersion() {
		return currentVersion;
	}
	
	/**
	 * Get the latest version, fetched from the Internet
	 */
	public String latestVersion() {
		return latestVersion;
	}
	
	/**
	 * Returns whether the mod is up to date with the latest version.
	 */
	public boolean upToDate() {
		return currentVersion.equals(latestVersion);
	}
	
}
