/* 
  This file is part of AvatarMod.
  
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

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
