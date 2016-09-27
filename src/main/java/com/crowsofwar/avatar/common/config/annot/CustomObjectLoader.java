package com.crowsofwar.avatar.common.config.annot;

/**
 * Allows a class to specify extra functionality when being loaded from configuration. The class
 * must be marked with {@link HasCustomLoader}.
 * 
 * @author CrowsOfWar
 */
public interface CustomObjectLoader<T> {
	
	void load(Object relevantConfigInfoWillGoHere, T obj);
	
}
