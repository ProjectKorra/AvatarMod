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

import javax.annotation.Nullable;

import com.crowsofwar.avatar.common.entity.AvatarEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Represents an AvatarEntity which is stored by ID but also cached for
 * performance.
 * <p>
 * Note: is not synced; designed to be manipulated by someone with a
 * synchronized ID.
 * 
 * @author CrowsOfWar
 */
public class CachedEntity<T extends AvatarEntity> {
	
	private T cachedEntity;
	private int entityId;
	
	public CachedEntity(int id) {
		this.entityId = id;
	}
	
	/**
	 * Reads this cached entity from NBT. Warning: Will use values directly from
	 * this compound, so make sure that a sub-compound is used specifically for
	 * this cached entity.
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		entityId = nbt.getInteger("EntityId");
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("EntityId", entityId);
	}
	
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	
	/**
	 * Gets the entity, searching for it if necessary. Then returns the entity.
	 * <p>
	 * Null if entity cannot be found.
	 */
	public @Nullable T getEntity(World world) {
		if (checkCacheValidity() && entityId > -1) {
			cachedEntity = AvatarEntity.lookupEntity(world, entityId);
		}
		return cachedEntity;
	}
	
	/**
	 * Sets the entity Id and cache. Can be set to null.
	 */
	public void setEntity(@Nullable T entity) {
		cachedEntity = entity;
		entityId = entity == null ? -1 : entity.getAvId();
	}
	
	/**
	 * Checks whether the cached entity is invalid (null or dead). If so, sets
	 * to null and returns true. Else returns false.
	 * 
	 * @return whether cache is invalid; if true the cached entity is null
	 */
	private boolean checkCacheValidity() {
		if (entityId < 0 || cachedEntity == null || cachedEntity.isDead
				|| cachedEntity.getAvId() != entityId) {
			cachedEntity = null;
			return true;
		}
		
		return false;
	}
	
}
