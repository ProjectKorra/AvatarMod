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

package com.crowsofwar.avatar.config;

/**
 * Represents a Configuration entry. It has a String key and a changeable value.
 *
 * @param <T> The type of value
 * @author CrowsOfWar
 */
public interface ConfigurableProperty<T> {

	T currentValue();

	/**
	 * Not using generics because of annoying things with converting Integer -> Double.
	 */
	void setValue(Object value);

}
