package com.crowsofwar.gorecore.data;

/**
 * Specifications for anything that is used to save data.
 */
public interface GoreCoreDataSaver {
	
	int getInt(String key);
	
	void setInt(String key, int value);
	
	String getString(String key);
	
	void setString(String key, String value);
	
	float getFloat(String key);
	
	void setFloat(String key, float value);
	
	double getDouble(String key);
	
	void setDouble(String key, double value);
	
	long getLong(String key);
	
	void setLong(String key, long value);
	
	void saveChanges();
	
}
