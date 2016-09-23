package com.crowsofwar.gorecore.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface ConfigurableFactory<T> {
	
	T load(Configuration cfg);
	
}
