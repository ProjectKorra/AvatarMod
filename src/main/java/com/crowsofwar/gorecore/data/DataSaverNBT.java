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

package com.crowsofwar.gorecore.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Wraps NBTTagCompounds to be usable with {@link DataSaver}.
 * 
 * @author CrowsOfWar
 */
public class DataSaverNBT implements DataSaver {
	
	private NBTTagCompound nbt;
	
	/**
	 * Creates a new NBTTagCompound DataSaver wrapper using a new
	 * NBTTagCompound.
	 */
	public DataSaverNBT() {
		this(new NBTTagCompound());
	}
	
	/**
	 * Creates a new NBTTagCompound DataSaver wrapper using the specified
	 * NBTTagCompound.
	 */
	public DataSaverNBT(NBTTagCompound nbt) {
		this.nbt = nbt;
	}
	
	@Override
	public int getInt(String key) {
		return nbt.getInteger(key);
	}
	
	@Override
	public void setInt(String key, int value) {
		nbt.setInteger(key, value);
	}
	
	@Override
	public String getString(String key) {
		return nbt.getString(key);
	}
	
	@Override
	public void setString(String key, String value) {
		nbt.setString(key, value);
	}
	
	@Override
	public float getFloat(String key) {
		return nbt.getFloat(key);
	}
	
	@Override
	public void setFloat(String key, float value) {
		nbt.setFloat(key, value);
	}
	
	@Override
	public double getDouble(String key) {
		return nbt.getDouble(key);
	}
	
	@Override
	public void setDouble(String key, double value) {
		nbt.setDouble(key, value);
	}
	
	@Override
	public long getLong(String key) {
		return nbt.getLong(key);
	}
	
	@Override
	public void setLong(String key, long value) {
		nbt.setLong(key, value);
	}
	
	@Override
	public void saveChanges() {}
	
}
