package com.maxandnoah.avatar.common.util;

import java.util.ArrayList;
import java.util.List;

import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AvatarUtils {
	
	/**
	 * Write list to NBT
	 * @param list
	 * @param parent
	 * @param listName
	 * @param writer Passed arguments are both empty arrays
	 */
	public static <T extends ReadableWritable> void writeToNBT(List<T> list, NBTTagCompound parent, String listName,
			WriteToNBT<T> writer) {
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
	 * Info parameter for the instantiator will be length of 1, containing a string.
	 * String is the full class name of what to create
	 * @param instantiator Methodsextradata is an empty array. Extradata is passed in args
	 * @param parent
	 * @param listName
	 * @param args Any extra parameters you want to pass to your CreateFromNBT
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
	
}
