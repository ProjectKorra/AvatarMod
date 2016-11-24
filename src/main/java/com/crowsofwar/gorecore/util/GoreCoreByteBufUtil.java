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

package com.crowsofwar.gorecore.util;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

/**
 * Contains utility methods for reading and writing to ByteBufs.
 * 
 * @author CrowsOfWar
 */
public final class GoreCoreByteBufUtil {
	
	public static String readString(ByteBuf buf) {
		String res = "";
		int length = buf.readInt();
		for (int i = 0; i < length; i++) {
			res += buf.readChar();
		}
		return res;
	}
	
	public static void writeString(ByteBuf buf, String str) {
		char[] chs = str.toCharArray();
		buf.writeInt(chs.length);
		for (int i = 0; i < chs.length; i++) {
			buf.writeChar(chs[i]);
		}
	}
	
	public static UUID readUUID(ByteBuf buf) {
		return new UUID(buf.readLong(), buf.readLong());
	}
	
	public static void writeUUID(ByteBuf buf, UUID uuid) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
	
}
