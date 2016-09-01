package com.crowsofwar.avatar.common.util.event;

/**
 * Describes the <strong>subject</strong> used in the Observer pattern.
 * <p>
 * {@link Observer Observers} can hook onto subjects to receive events.
 * 
 * @author CrowsOfWar
 */
public interface Subject {
	
	/**
	 * Attach the observer to this subject.
	 * 
	 * @param obs
	 *            Observer to add
	 * @param eventClass
	 *            The class of event to subscribe to
	 */
	<E> void addObserver(Observer<E> obs, Class<E> eventClass);
	
	/**
	 * Remove the observer from this subject.
	 * 
	 * @param obs
	 *            Observer to remove
	 * @param eventClass
	 *            Class of event to remove
	 */
	<E> void removeObserver(Observer<E> obs, Class<E> eventClass);
	
	/**
	 * Notify all listening observers of the given event.
	 * 
	 * @param e
	 */
	void notifyObservers(Object e);
	
}
