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

package com.crowsofwar.avatar.common.util;

import static com.crowsofwar.avatar.AvatarLog.WarningType.INVALID_SAVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	 * Info parameter for the instantiator will be length of 1, containing a string. String is the
	 * full class name of what to create
	 * 
	 * @param instantiator
	 *            Methodsextradata is an empty array. Extradata is passed in args
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
	 * Clears the list(or collection), and adds items from the NBT list. Does not permit null
	 * values.
	 * 
	 * 
	 * @param itemProvider
	 *            Loads items from the list. Takes an NBT for that item, and returns the actual item
	 *            object.
	 * @param nbt
	 *            NBT compound to load the list from
	 * @param listName
	 *            The name of the list tag
	 */
	public static <T> void readList(Collection<T> list, Function<NBTTagCompound, T> itemProvider,
			NBTTagCompound nbt, String listName) {
		
		list.clear();
		
		NBTTagList listTag = nbt.getTagList(listName, 10);
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
	 *            Responsible for actually writing the desired data to NBT. Takes the NBT to write
	 *            to & the item.
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
	
}
