package com.crowsofwar.avatar.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;

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
