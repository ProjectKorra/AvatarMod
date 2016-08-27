package com.crowsofwar.avatar.common.util.event;

/**
 * Describes the <strong>subject</strong> used in the Observer pattern.
 * <p>
 * {@link Observer Observers} can hook onto subjects to receive {@link IEvent Events}.
 * 
 * @author CrowsOfWar
 */
public interface Subject<EVENT extends IEvent> {
	
	void addObserver(Observer obs);
	
	void removeObserver(Observer obs);
	
	void notifyObservers(EVENT e);
	
}
