package com.crowsofwar.gorecore.util;

import net.minecraft.nbt.NBTTagCompound;

public class GoreCoreNBTInterfaces {
	
	public interface ReadableWritable {
		void readFromNBT(NBTTagCompound nbt);
		
		void writeToNBT(NBTTagCompound nbt);
	}
	
	public interface CreateFromNBT<T> {
		/**
		 * Called to create a new <code>T</code> from the NBTTagCompound.
		 * 
		 * @param nbt
		 *            The NBTTagCompound to create it from
		 * @param methodsExtraData
		 *            Method-specific extra data provided by the method, the details should be
		 *            provided in a javadoc
		 * @param extraData
		 *            Extra data provided by parameters in the method; specific per implementation
		 *            of CreateFromNBT
		 * @return A new instance of <code>T</code>
		 */
		T create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData);
	}
	
	public interface WriteToNBT<T> {
		/**
		 * Called to write the object of type <code>T</code> to the NBTTagCompound.
		 * 
		 * @param nbt
		 *            The NBTTagCompound to write the data to
		 * @param object
		 *            The object to write to NBT
		 * @param methodsExtraData
		 *            Method-specific extra data provided by the method, the details should be
		 *            provided in a javadoc
		 * @param extraData
		 *            Extra data provided by parameters in the method; specific per implementation
		 *            of WriteToNBT
		 */
		void write(NBTTagCompound nbt, T object, Object[] methodsExtraData, Object[] extraData);
	}
	
	public interface MapUser<K, V> {
		
		/**
		 * Create a new instance of the key object using the given constructor arguments.
		 * 
		 * @param nbt
		 *            The NBTTagCompound which is used to store the key's data
		 * @param constructArgsK
		 *            Constructor arguments passed in by whoever is using
		 *            {@link GoreCoreNBTUtil#readListFromNBT(NBTTagCompound, String, CreateFromNBT, Object...)}
		 * @return a new instance of K
		 */
		K createK(NBTTagCompound nbt, Object[] constructArgsK);
		
		/**
		 * Create a new instance of the value object using the given constructor arguments.
		 * 
		 * @param nbt
		 *            The NBTTagCompound which is used to store the value's data
		 * @param k
		 *            The key object bound to the value
		 * @param constructArgsV
		 *            Constructor arguments passed in by whoever is using
		 *            {@link GoreCoreNBTUtil#readListFromNBT(NBTTagCompound, String, CreateFromNBT, Object...)}
		 * @return a new instance of V
		 */
		V createV(NBTTagCompound nbt, K key, Object[] constructArgsV);
		
		/**
		 * Write the given key object to NBT.
		 * 
		 * @param nbt
		 *            The NBTTagCompound to store the key's data in
		 */
		void writeK(NBTTagCompound nbt, K obj);
		
		/**
		 * Write the given value object to NBT.
		 * 
		 * @param nbt
		 *            The NBTTagCompound to store the value's data in
		 */
		void writeV(NBTTagCompound nbt, V obj);
		
	}
	
}
