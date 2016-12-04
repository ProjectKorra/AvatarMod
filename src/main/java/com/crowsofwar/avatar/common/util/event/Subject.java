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
	void post(Object e);
	
}
