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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.data.CachedEntity;
import com.google.common.base.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Like {@link CachedEntity}, but allows access to an Entity on both sides (synchronized). It can
 * only be used by another Entity since it uses the DataManager.
 *
 * @author CrowsOfWar
 */
@SuppressWarnings("Guava")
public class SyncedEntity<T extends Entity> {

	private final Entity using;
	private final DataParameter<Optional<UUID>> sync;
	private final CachedEntity<T> cache;
	private boolean allowNullSaving;

	/**
	 * Create an entity reference.
	 *
	 * @param entity The entity that is USING the reference, usually
	 *               <code>this</code>. Not the entity being referenced
	 * @param sync   DataParameter used to sync. Should NOT be created specifically
	 *               for this SyncedEntity - use a constant. Will not
	 *               register to entity DataManager.
	 */
	public SyncedEntity(Entity entity, DataParameter<Optional<UUID>> sync) {
		this.using = entity;
		this.sync = sync;
		this.cache = new CachedEntity<>(null);
		this.allowNullSaving = true;
	}

	/**
	 * Intended for references which need to be set. If the referenced entity is not found on
	 * loading, destroys the using entity to avoid crashes.
	 */
	public void preventNullSaving() {
		allowNullSaving = false;
	}

	@Nullable
	public T getEntity() {
		// Cache may have an incorrect id; other side could have changed
		// dataManager id, but not the cached entity id.
		Optional<UUID> optional = using.getDataManager().get(sync);
		cache.setEntityId(optional.orNull());
		return cache.getEntity(using.world);
	}

	public void setEntity(@Nullable T entity) {
		cache.setEntity(entity);
		using.getDataManager().set(sync, Optional.fromNullable(cache.getEntityId()));
	}

	/**
	 * Get the UUID of the entity. For players, returns their account Id.
	 */
	@Nullable
	public UUID getEntityId() {
		return cache.getEntityId();
	}

	public void setEntityId(@Nullable UUID entityId) {
		cache.setEntityId(entityId);
		using.getDataManager().set(sync, Optional.fromNullable(entityId));
	}

	/**
	 * Reads this reference from NBT. Please note, reads values directly from
	 * this compound (no sub-compound).
	 */
	public void readFromNbt(NBTTagCompound nbt) {
		cache.readFromNbt(nbt);
		using.getDataManager().set(sync, Optional.fromNullable(cache.getEntityId()));
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
	public void writeToNbt(NBTTagCompound nbt) {
		cache.writeToNbt(nbt);
	}

}
