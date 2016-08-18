package com.crowsofwar.gorecore.data;

/**
 * An implementation of {@link GoreCoreDataSaver DataSaver} where data is not stored anywhere.
 * 
 * @author CrowsOfWar
 */
public class GoreCoreDataSaverDontSave implements GoreCoreDataSaver {
	
	@Override
	public int getInt(String key) {
		return 0;
	}
	
	@Override
	public void setInt(String key, int value) {}
	
	@Override
	public String getString(String key) {
		return null;
	}
	
	@Override
	public void setString(String key, String value) {}
	
	@Override
	public float getFloat(String key) {
		return 0;
	}
	
	@Override
	public void setFloat(String key, float value) {}
	
	@Override
	public double getDouble(String key) {
		return 0;
	}
	
	@Override
	public void setDouble(String key, double value) {}
	
	@Override
	public long getLong(String key) {
		return 0;
	}
	
	@Override
	public void setLong(String key, long value) {}
	
	@Override
	public void saveChanges() {}
	
}
