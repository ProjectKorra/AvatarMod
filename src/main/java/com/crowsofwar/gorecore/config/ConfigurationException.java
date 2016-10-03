package com.crowsofwar.gorecore.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigurationException extends RuntimeException {
	
	private ConfigurationException(String message) {
		super(message);
	}
	
	private ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * The end-user made a mistake by creating an invalid configuration file.
	 * 
	 * @author CrowsOfWar
	 */
	public static class UserMistake extends ConfigurationException {
		
		public UserMistake(String message) {
			super(message);
		}
		
	}
	
	/**
	 * An exception occurred while trying to access the configuration file on disk.
	 * 
	 * @author CrowsOfWar
	 */
	public static class LoadingException extends ConfigurationException {
		
		public LoadingException(String message, Throwable cause) {
			super(message, cause);
		}
		
	}
	
	/**
	 * An exception occurred while using reflection to set values of the object.
	 * 
	 * @author CrowsOfWar
	 */
	public static class ReflectionException extends ConfigurationException {
		
		public ReflectionException(String message, Throwable cause) {
			super(message, cause);
		}
		
	}
	
}
