package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.AvatarEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Represents an entity which is stored by ID but also cached for performance.
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
	public T getEntity(World world) {
		if (checkCacheValidity() && entityId > -1) {
			cachedEntity = AvatarEntity.lookupEntity(world, entityId);
		}
		return cachedEntity;
	}
	
	public void setEntity(T entity) {
		cachedEntity = entity;
		entityId = entity.getAvId();
	}
	
	/**
	 * Checks whether the cached entity is invalid (null or dead). If so, sets
	 * to null and returns true. Else returns false.
	 * 
	 * @return whether cache is invalid; if true the cached entity is null
	 */
	private boolean checkCacheValidity() {
		if (entityId < 0 || cachedEntity == null || cachedEntity.isDead) {
			cachedEntity = null;
			return true;
		}
		
		return false;
	}
	
}
