package com.crowsofwar.gorecore.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface ListLoader<T> {
	
	T load(Object input);
	
}
