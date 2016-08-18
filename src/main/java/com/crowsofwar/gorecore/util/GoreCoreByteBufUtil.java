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
