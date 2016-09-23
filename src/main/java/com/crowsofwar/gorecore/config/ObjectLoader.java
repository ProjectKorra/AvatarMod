package com.crowsofwar.gorecore.config;

/**
 * Create and load an object from a {@link Configuration}.
 * 
 * @param T
 *            Type of an object to load
 * 
 * @author CrowsOfWar
 */
@FunctionalInterface
public interface ObjectLoader<T> {
	
	/**
	 * Create/load an object from the configuration values.
	 * 
	 * @param cfg
	 *            Configuration instance
	 */
	T load(Configuration cfg);
	
}
