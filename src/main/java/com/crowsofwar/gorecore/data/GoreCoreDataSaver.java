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
