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

import java.util.UUID;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.data.CachedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;

/**
 * Like {@link CachedEntity}, but allows access to the server/client
 * counterparts of an AvatarEntity, on both sides.
 * <p>
 * Designed for use to have 2 entities having synced references to each other.
 * Uses DataManager to sync the entities' IDs and then performs lookup/caching.
 * <p>
 * NOTE: By default, if the entity is being loaded and the reference is null,
 * the SyncableEntityReference will setDead() the entity to prevent a NPE crash.
 * This can be disabled by calling {@link #allowNullSaving()}.
 * 
 * @author CrowsOfWar
 */
public class SyncableEntityReference<T extends Entity> {
	
	private final Entity using;
	private final DataParameter<UUID> sync;
	private final CachedEntity<T> cache;
	private boolean allowNullSaving;
	
	/**
	 * Create an entity reference.
	 * 
	 * @param entity
	 *            The entity that is USING the reference, usually
	 *            <code>this</code>. Not the entity being referenced
	 * @param sync
	 *            DataParameter used to sync. Should NOT be created specifically
	 *            for this SyncableEntityReference - use a constant. Will not
	 *            register to entity DataManager.
	 */
	public SyncableEntityReference(Entity entity, DataParameter<UUID> sync) {
		this.using = entity;
		this.sync = sync;
		this.cache = new CachedEntity<>(null);
		this.allowNullSaving = false;
	}
	
	/**
	 * Enable saving a null reference. Normally, if the reference is null while
	 * being loaded, the entity is setDead() to try to prevent erroring entities
	 * from causing crashes.
	 */
	public void allowNullSaving() {
		allowNullSaving = true;
	}
	
	@Nullable
	public T getEntity() {
		// Cache may have an incorrect id; other side could have changed
		// dataManager id, but not the cached entity id.
		cache.setEntityId(using.getDataManager().get(sync));
		return cache.getEntity(using.worldObj);
	}
	
	public void setEntity(@Nullable T entity) {
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
		if (!allowNullSaving && getEntity() == null) {
			using.setDead();
			AvatarLog.warn(WarningType.INVALID_SAVE,
					"Entity reference was null on load and removed entity for safety: " + using);
		}
	}
	
	/**
	 * Writes this reference from NBT. Please note, writes values directly from
	 * this compound (no sub-compound).
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		cache.writeToNBT(nbt);
	}
	
}
