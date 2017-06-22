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

package com.crowsofwar.avatar.common.data;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Represents an entity which is stored by its UUID but also cached for
 * performance.
 * <p>
 * Note: is not synchronized
 * 
 * @author CrowsOfWar
 */
public class CachedEntity<T extends Entity> {
	
	private T cachedEntity;
	private UUID entityId;
	
	public CachedEntity(UUID id) {
		this.entityId = id;
	}
	
	/**
	 * Reads this cached entity from NBT. Warning: Will use values directly from
	 * this compound, so make sure that a sub-compound is used specifically for
	 * this cached entity.
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		entityId = nbt.getUniqueId("EntityUuid");
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setUniqueId("EntityUuid", entityId);
	}
	
	public void fromBytes(ByteBuf buf) {
		entityId = new UUID(buf.readLong(), buf.readLong());
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeLong(entityId.getMostSignificantBits());
		buf.writeLong(entityId.getLeastSignificantBits());
	}
	
	public UUID getEntityId() {
		return entityId;
	}
	
	public void setEntityId(UUID entityId) {
		this.entityId = entityId;
	}
	
	/**
	 * Gets the entity, searching for it if necessary. Then returns the entity.
	 * <p>
	 * Null if entity cannot be found.
	 */
	public @Nullable T getEntity(World world) {
		if (checkCacheValidity() && entityId != null) {
			List<Entity> list = world.getEntities(Entity.class, entity -> entity.getUniqueID() == entityId);
			cachedEntity = list.isEmpty() ? null : (T) list.get(0);
		}
		return cachedEntity;
	}
	
	/**
	 * Sets the entity Id and cache. Can be set to null.
	 */
	public void setEntity(@Nullable T entity) {
		cachedEntity = entity;
		entityId = entity == null ? null : entity.getUniqueID();
	}
	
	/**
	 * Checks whether the cached entity is invalid (null or dead). If so, sets
	 * to null and returns true. Else returns false.
	 * 
	 * @return whether cache is invalid; if true the cached entity is null
	 */
	private boolean checkCacheValidity() {
		if (entityId == null || cachedEntity == null || cachedEntity.isDead
				|| cachedEntity.getUniqueID() != entityId) {
			cachedEntity = null;
			return true;
		}
		
		return false;
	}
	
}
