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

import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.FMLLog;

/**
 * A base class for WorldSavedData.
 * 
 * @author CrowsOfWar
 */
public abstract class WorldData extends WorldSavedData implements DataSaver {
	
	/**
	 * The world that this data belongs to.
	 * <p>
	 * If the world data was constructed because it was first loaded by vanilla,
	 * will still be null until
	 * {@link #getDataForWorld(Class, String, World, boolean)} is called.
	 */
	private World world;
	
	/**
	 * Data stored via the {@link DataSaver} methods. FIXME never
	 * saves...?
	 */
	private DataSaverNBT storedData;
	
	public WorldData(String key) {
		super(key);
		this.storedData = new DataSaverNBT();
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * Marks the world data dirty so that it will be saved to disk.
	 */
	@Override
	public void saveChanges() {
		markDirty();
	}
	
	/**
	 * Use to make an easy implementation of getDataForWorld:
	 * 
	 * <pre>
	 * public static MyWorldData getDataForWorld(World world) {
	 * 	return getDataForWorld(MyWorldData.class, "MyWorldData", world, true);
	 * }
	 * </pre>
	 * 
	 * @param worldDataClass
	 *            The class object of your world data
	 * @param key
	 *            The key to store the world data under
	 * @param world
	 *            The world to get world data for
	 * @param separatePerDimension
	 *            Whether world data is saved for each dimension or for all
	 *            dimensions
	 * @return World data, retrieved using the specified options
	 */
	protected static <T extends WorldData> T getDataForWorld(Class<T> worldDataClass, String key,
			World world, boolean separatePerDimension) {
		try {
			MapStorage ms = separatePerDimension ? world.getPerWorldStorage() : world.getMapStorage();
			T data = worldDataClass.cast(ms.getOrLoadData(worldDataClass, key));
			
			if (data == null) {
				// TODO [1.10] Not sure if this is actually called anymore- need
				// to check.
				data = worldDataClass.getConstructor(String.class).newInstance(key);
				data.setDirty(true);
				ms.setData(key, data);
			}
			
			data.setWorld(world);
			
			return data;
		} catch (Exception e) {
			FMLLog.bigWarning("GoreCore> Could not create World Data class!");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int getInt(String key) {
		return storedData.getInt(key);
	}
	
	@Override
	public void setInt(String key, int value) {
		storedData.setInt(key, value);
	}
	
	@Override
	public String getString(String key) {
		return storedData.getString(key);
	}
	
	@Override
	public void setString(String key, String value) {
		storedData.setString(key, value);
	}
	
	@Override
	public float getFloat(String key) {
		return storedData.getFloat(key);
	}
	
	@Override
	public void setFloat(String key, float value) {
		storedData.setFloat(key, value);
	}
	
	@Override
	public double getDouble(String key) {
		return storedData.getDouble(key);
	}
	
	@Override
	public void setDouble(String key, double value) {
		storedData.setDouble(key, value);
	}
	
	@Override
	public long getLong(String key) {
		return storedData.getLong(key);
	}
	
	@Override
	public void setLong(String key, long value) {
		storedData.setLong(key, value);
	}
	
}
