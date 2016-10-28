package com.crowsofwar.gorecore.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Wraps NBTTagCompounds to be usable with {@link GoreCoreDataSaver}.
 * 
 * @author CrowsOfWar
 */
public class GoreCoreDataSaverNBT implements GoreCoreDataSaver {
	
	private NBTTagCompound nbt;
	
	/**
	 * Creates a new NBTTagCompound DataSaver wrapper using a new NBTTagCompound.
	 */
	public GoreCoreDataSaverNBT() {
		this(new NBTTagCompound());
	}
	
	/**
	 * Creates a new NBTTagCompound DataSaver wrapper using the specified NBTTagCompound.
	 */
	public GoreCoreDataSaverNBT(NBTTagCompound nbt) {
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
