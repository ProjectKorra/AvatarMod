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

package com.crowsofwar.avatar.network;

import com.crowsofwar.avatar.util.data.BendingData;
import io.netty.buffer.ByteBuf;

/**
 * @author CrowsOfWar
 */
public interface DataTransmitter<T> {

	/**
	 * Writes the <code>T</code> to network
	 */
	void write(ByteBuf buf, T t);

	/**
	 * Creates a new <code>T</code> and reads data from the network
	 */
	T read(ByteBuf buf, BendingData data);

}
