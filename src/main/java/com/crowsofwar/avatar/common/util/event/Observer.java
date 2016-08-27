package com.crowsofwar.avatar.common.util.event;

/**
 * Describes an <strong>Observer</strong> from the Observer pattern.
 * <p>
 * Observers subscribe to a {@link Subject} and receive {@link IEvent Events} from it.
 * 
 * @author CrowsOfWar
 */
public interface Observer<EVENT extends IEvent> {
	
	void notify(EVENT e);
	
}
