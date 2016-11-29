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
 * An implementation of {@link DataSaver DataSaver} where data is not stored anywhere.
 * 
 * @author CrowsOfWar
 */
public class DataSaverDontSave implements DataSaver {
	
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
