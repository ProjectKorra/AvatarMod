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

package com.crowsofwar.avatar.common.entityproperty;

/**
 * The basic interface for an entity property. A property is of a certain type, and can be retrieved
 * and set. Different implementations would have specific details about how the property should be
 * get and set.
 * 
 * @param <T>
 *            The type of object that the property stores.
 */
public interface IEntityProperty<T> {
	
	/**
	 * Get the current value from the entity property.
	 * 
	 * @return
	 */
	T getValue();
	
	/**
	 * Set the value of the property.
	 * 
	 * @param value
	 */
	void setValue(T value);
	
}
