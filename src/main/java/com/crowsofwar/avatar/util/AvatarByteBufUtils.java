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

package com.crowsofwar.avatar.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A collection of methods to help with reading/writing to ByteBufs.
 *
 * @author CrowsOfWar
 */
public class AvatarByteBufUtils {

	public static <T> List<T> readList(ByteBuf buf, Supplier<T> itemSupplier) {
		List<T> list = new ArrayList<>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			list.add(itemSupplier.get());
		}
		return list;
	}

	public static <T> void writeList(ByteBuf buf, List<T> list, Consumer<T> writer) {
		buf.writeInt(list.size());
		for (T t : list) {
			writer.accept(t);
		}
	}

}
