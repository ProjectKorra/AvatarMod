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
		 *            Method-specific extra data provided by the method, the
		 *            details should be provided in a javadoc
		 * @param extraData
		 *            Extra data provided by parameters in the method; specific
		 *            per implementation of CreateFromNBT
		 * @return A new instance of <code>T</code>
		 */
		T create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData);
	}
	
	public interface WriteToNBT<T> {
		/**
		 * Called to write the object of type <code>T</code> to the
		 * NBTTagCompound.
		 * 
		 * @param nbt
		 *            The NBTTagCompound to write the data to
		 * @param object
		 *            The object to write to NBT
		 * @param methodsExtraData
		 *            Method-specific extra data provided by the method, the
		 *            details should be provided in a javadoc
		 * @param extraData
		 *            Extra data provided by parameters in the method; specific
		 *            per implementation of WriteToNBT
		 */
		void write(NBTTagCompound nbt, T object, Object[] methodsExtraData, Object[] extraData);
	}
	
	public interface MapUser<K, V> {
		
		/**
		 * Create a new instance of the key object using the given constructor
		 * arguments.
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
		 * Create a new instance of the value object using the given constructor
		 * arguments.
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
