package com.crowsofwar.avatar.common.util.event;

/**
 * Describes an <strong>Observer</strong> from the Observer pattern.
 * <p>
 * Observers subscribe to a {@link Subject} and receive events from it.
 * 
 * @author CrowsOfWar
 */
@FunctionalInterface
public interface Observer<EVENT> {
	
	void invoke(EVENT e);
	
}
