package com.crowsofwar.avatar.common.util.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.annotation.ThreadSafe;

/**
 * An implementation of {@link Subject} which allows you to notify subjects of events.
 * <p>
 * Designed to use with delegation.
 * <p>
 * TODO This IS thread safe ... right?
 * 
 * @author CrowsOfWar
 */
@ThreadSafe
public final class EventNotifier implements Subject {
	
	private final Map<Class, Set<Observer>> observers;
	
	public EventNotifier() {
		this.observers = new HashMap<>();
	}
	
	private Set<Observer> getObserversForEvent(Class eventClass) {
		if (!observers.containsKey(eventClass)) {
			observers.put(eventClass, new HashSet<>());
		}
		return observers.get(eventClass);
	}
	
	@Override
	public synchronized <E> void addObserver(Observer<E> obs, Class<E> eventClass) {
		getObserversForEvent(eventClass).add(obs);
	}
	
	@Override
	public synchronized <E> void removeObserver(Observer<E> obs, Class<E> eventClass) {
		getObserversForEvent(eventClass).remove(obs);
	}
	
	@Override
	public synchronized void post(Object e) {
		Iterator<Observer> it = getObserversForEvent(e.getClass()).iterator();
		while (it.hasNext()) {
			it.next().invoke(e);
		}
	}
	
}
