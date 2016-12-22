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

package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.data.CachedEntity;
import com.crowsofwar.avatar.common.entity.AvatarEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;

/**
 * Like {@link CachedEntity}, but allows access to the server/client
 * counterparts of an AvatarEntity, on both sides.
 * <p>
 * Designed for use to have 2 entities having synced references to each other.
 * Uses DataManager to sync the entities' IDs and then performs lookup/caching.
 * 
 * @author CrowsOfWar
 */
public class SyncableEntityReference<T extends AvatarEntity> {
	
	private final AvatarEntity using;
	private final DataParameter<Integer> sync;
	private final CachedEntity<T> cache;
	
	/**
	 * Create an entity reference.
	 * 
	 * @param entity
	 *            The entity that is USING the reference, usually
	 *            <code>this</code>. Not the entity being referenced
	 * @param sync
	 *            DataParameter used to sync. Should NOT be created specifically
	 *            for this SyncableEntityReference - use a constant
	 */
	public SyncableEntityReference(AvatarEntity entity, DataParameter<Integer> sync) {
		this.using = entity;
		this.sync = sync;
		this.cache = new CachedEntity<T>(-1);
	}
	
	public T getEntity() {
		// Cache may have an incorrect id; other side could have changed
		// dataManager id, but not the cached entity id.
		cache.setEntityId(using.getDataManager().get(sync));
		return cache.getEntity(using.worldObj);
	}
	
	public void setEntity(T entity) {
		cache.setEntity(entity);
		using.getDataManager().set(sync, cache.getEntityId());
	}
	
	/**
	 * Reads this reference from NBT. Please note, reads values directly from
	 * this compound (no sub-compound).
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		cache.readFromNBT(nbt);
		using.getDataManager().set(sync, cache.getEntityId());
	}
	
	/**
	 * Writes this reference from NBT. Please note, writes values directly from
	 * this compound (no sub-compound).
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		cache.writeToNBT(nbt);
	}
	
}
