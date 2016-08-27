package com.crowsofwar.avatar.common.util.event;

/**
 * Describes the <strong>subject</strong> used in the Observer pattern.
 * <p>
 * {@link Observer Observers} can hook onto subjects to receive {@link IEvent Events}.
 * 
 * @author CrowsOfWar
 */
public interface Subject<EVENT extends IEvent> {
	
	/**
	 * Attach the observer to this subject.
	 * 
	 * @param obs
	 */
	void addObserver(Observer obs);
	
	/**
	 * Remove the observer from this subject.
	 * 
	 * @param obs
	 */
	void removeObserver(Observer obs);
	
	/**
	 * Notify all listening observers of the given event.
	 * 
	 * @param e
	 */
	void notifyObservers(EVENT e);
	
}
