package com.crowsofwar.avatar.common.util.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public final class EventNotifier<EVENT extends IEvent> implements Subject<EVENT> {
	
	private final List<Observer> observers;
	
	public EventNotifier() {
		this.observers = new ArrayList<>();
	}
	
	@Override
	public synchronized void addObserver(Observer obs) {
		observers.add(obs);
	}
	
	@Override
	public synchronized void removeObserver(Observer obs) {
		observers.remove(obs);
	}
	
	@Override
	public synchronized void notifyObservers(EVENT e) {
		Iterator<Observer> it = observers.iterator();
		while (it.hasNext()) {
			it.next().notify(e);
		}
	}
	
}
