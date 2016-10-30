package com.crowsofwar.avatar.common.util;

import static com.crowsofwar.avatar.AvatarLog.WarningType.INVALID_SAVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AvatarUtils {
	
	/**
	 * Write list to NBT
	 * 
	 * @param list
	 * @param parent
	 * @param listName
	 * @param writer
	 *            Passed arguments are both empty arrays
	 */
	public static <T extends ReadableWritable> void writeToNBT(List<T> list, NBTTagCompound parent,
			String listName, WriteToNBT<T> writer) {
		NBTTagList listTag = new NBTTagList();
		
		for (int i = 0; i < list.size(); i++) {
			NBTTagCompound writeTo = new NBTTagCompound();
			list.get(i).writeToNBT(writeTo);
			writer.write(writeTo, list.get(i), new Object[0], new Object[0]);
			listTag.appendTag(writeTo);
		}
		
		parent.setTag(listName, listTag);
		
	}
	
	/**
	 * Info parameter for the instantiator will be length of 1, containing a
	 * string. String is the full class name of what to create
	 * 
	 * @param instantiator
	 *            Methodsextradata is an empty array. Extradata is passed in
	 *            args
	 * @param parent
	 * @param listName
	 * @param args
	 *            Any extra parameters you want to pass to your CreateFromNBT
	 * @return
	 */
	public static <T extends ReadableWritable> List<T> readFromNBT(CreateFromNBT<T> instantiator,
			NBTTagCompound parent, String listName, Object... args) {
		List<T> out = new ArrayList<T>();
		NBTTagList listTag = parent.getTagList(listName, parent.getId());
		
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound readFrom = listTag.getCompoundTagAt(i);
			T rw = instantiator.create(readFrom, new Object[0], args);
			rw.readFromNBT(readFrom);
			out.add(rw);
		}
		
		return out;
	}
	
	/**
	 * Smoothstep function. Used for smoother interpolation.
	 * 
	 * @param y1
	 *            First y-position
	 * @param y2
	 *            Second y-position
	 * @param x
	 *            Interpolation value, 0-1
	 * @return Y position interpolated between y1 and y2 using x
	 */
	public static double smoothstep(double y1, double y2, double x) {
		double y = x * x * (3 - 2 * x);
		y = y < 0 ? 0 : (y > 1 ? 1 : y);
		return y * (y2 - y1) + y1;
	}
	
	/**
	 * Clears the list(or collection), and adds items from the NBT list. Does
	 * not permit null values.
	 * 
	 * 
	 * @param itemProvider
	 *            Loads items from the list. Takes an NBT for that item, and
	 *            returns the actual item object.
	 * @param nbt
	 *            NBT compound to load the list from
	 * @param listName
	 *            The name of the list tag
	 */
	public static <T> void readList(Collection<T> list, Function<NBTTagCompound, T> itemProvider,
			NBTTagCompound nbt, String listName) {
		
		list.clear();
		
		NBTTagList listTag = nbt.getTagList(listName, 10);
		System.out.println("list tag: " + listTag.toString());
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);
			T read = itemProvider.apply(item);
			if (read != null) {
				list.add(read);
			} else {
				AvatarLog.warn(INVALID_SAVE,
						"Invalid list " + listName + ", contains unknown value: " + item);
			}
		}
		
	}
	
	/**
	 * Writes the list to NBT.
	 * 
	 * @param list
	 *            The list to write
	 * @param writer
	 *            Responsible for actually writing the desired data to NBT.
	 *            Takes the NBT to write to & the item.
	 * @param nbt
	 *            NBT compound to write list to
	 * @param listName
	 *            The name of the list tag
	 */
	public static <T> void writeList(Collection<T> list, BiConsumer<NBTTagCompound, T> writer,
			NBTTagCompound nbt, String listName) {
		
		NBTTagList listTag = new NBTTagList();
		
		for (T item : list) {
			
			NBTTagCompound nbtItem = new NBTTagCompound();
			writer.accept(nbtItem, item);
			listTag.appendTag(nbtItem);
			
		}
		
		nbt.setTag(listName, listTag);
		
	}
	
	/**
	 * Clears the map and adds the saved entries. Permits null values.
	 * 
	 * @param map
	 *            Map to read into
	 * @param keyProvider
	 *            Creates a key object from a given NBT. The NBT is only used by
	 *            the key.
	 * @param valueProvider
	 *            Creates a value object from a given NBT. The NBT is only used
	 *            by the value.
	 * @param nbt
	 *            NBT to read from
	 * @param mapName
	 *            Name to store it as
	 */
	public static <K, V> void readMap(Map<K, V> map, Function<NBTTagCompound, K> keyProvider,
			Function<NBTTagCompound, V> valueProvider, NBTTagCompound nbt, String mapName) {
		
		map.clear();
		
		NBTTagList listTag = nbt.getTagList(mapName, 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);
			map.put(keyProvider.apply(nbt.getCompoundTag("Key")),
					valueProvider.apply(nbt.getCompoundTag("Value")));
		}
		
	}
	
	/**
	 * Writes the map's entries to the NBT. Permits null values.
	 * 
	 * @param map
	 *            Map to write
	 * @param keyWriter
	 *            Given a key object & NBT, writes the key to disk.
	 * @param valueWriter
	 *            Given a value object & NBT, writes the value to disk.
	 * @param nbt
	 *            NBT to read from
	 * @param mapName
	 *            Name to store map as
	 */
	public static <K, V> void writeMap(Map<K, V> map, BiConsumer<NBTTagCompound, K> keyWriter,
			BiConsumer<NBTTagCompound, V> valueWriter, NBTTagCompound nbt, String mapName) {
		
		NBTTagList listTag = new NBTTagList();
		Set<Map.Entry<K, V>> entries = map.entrySet();
		
		for (Map.Entry<K, V> entry : entries) {
			NBTTagCompound item = new NBTTagCompound();
			NBTTagCompound keyNbt = new NBTTagCompound();
			NBTTagCompound valNbt = new NBTTagCompound();
			keyWriter.accept(keyNbt, entry.getKey());
			valueWriter.accept(valNbt, entry.getValue());
			item.setTag("Key", keyNbt);
			item.setTag("Value", valNbt);
			listTag.appendTag(item);
		}
		
		nbt.setTag(mapName, listTag);
		
	}
	
}
