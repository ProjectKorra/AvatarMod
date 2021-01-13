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

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Contains utility methods for reading and writing to ByteBufs.
 *
 * @author CrowsOfWar
 */
public final class GoreCoreByteBufUtil {

    public static String readString(ByteBuf buf) {
        StringBuilder res = new StringBuilder();
        int length = buf.readInt();
        //For some reason I was getting out of bounds exceptions???
        for (int i = 0; i < length; i++) {
            res.append(buf.readChar());
        }

        return res.toString();
    }

    public static void writeString(ByteBuf buf, String str) {
        char[] chs = str.toCharArray();
        buf.writeInt(chs.length);
        for (char ch : chs) {
            buf.writeChar(ch);
        }
    }

  /*  public static void writeString(ByteBuf buf, String string) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(string);
    }

    public static String readString(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        return buffer.readString(128);
    }**/


    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
        if (pos != null)
            buf.writeLong(pos.toLong());
    }

    public static BlockPos readBlockPos(ByteBuf buf) {
        if (buf != null)
            return BlockPos.fromLong(buf.readLong());
        return new BlockPos(0, 0, 0);
    }
}
