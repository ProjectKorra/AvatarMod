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

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;

/**
 * Designed to use with an entity. Manages a synchronized owner property,
 * allowing retrieval and "storage" of a player entity.
 * 
 * @author CrowsOfWar
 */
public class OwnerAttribute {
	
	private final DataParameter<String> sync;
	private final Entity entity;
	private final World world;
	private final Consumer<EntityPlayer> setOwnerCallback;
	
	private EntityPlayer ownerCached;
	
	/**
	 * Create a new owner attribute.
	 * 
	 * @param entity
	 *            Entity which has this attribute
	 * @param sync
	 *            Synchronization key. You don't have to register to entity's
	 *            data manager.
	 */
	public OwnerAttribute(Entity entity, DataParameter<String> sync) {
		this(entity, sync, player -> {
		});
	}
	
	/**
	 * Create a new owner attribute.
	 * 
	 * @param entity
	 *            Entity which has this attribute
	 * @param sync
	 *            Synchronization key. You don't have to register to entity's
	 *            data manager.
	 * @param setOwnerCallback
	 *            Called when the owner has been changed.
	 */
	public OwnerAttribute(Entity entity, DataParameter<String> sync,
			Consumer<EntityPlayer> setOwnerCallback) {
		this.entity = entity;
		this.sync = sync;
		this.world = entity.worldObj;
		this.setOwnerCallback = setOwnerCallback;
		this.entity.getDataManager().register(sync, "");
	}
	
	public void save(NBTTagCompound nbt) {
		nbt.setString("Owner", getOwnerName());
	}
	
	public void load(NBTTagCompound nbt) {
		setOwnerName(nbt.getString("Owner"));
		getOwner(); // Look up owner in world
	}
	
	private String getOwnerName() {
		return entity.getDataManager().get(sync);
	}
	
	private void setOwnerName(String name) {
		entity.getDataManager().set(sync, name);
	}
	
	/**
	 * Get owner. Null if player entity cannot be found. Only the owner's name
	 * is synced, so may be null on client but not server.
	 * <p>
	 * Detail: If the cached owner is null, but owner name is not, attempts to
	 * look for a player in the world with that name. Will then call
	 * {@link #setOwner(EntityPlayer)}.
	 */
	public EntityPlayer getOwner() {
		
		if (isCacheInvalid()) {
			// Slightly cosmetic, but only call setOwner(...) if the player was
			// found
			EntityPlayer player = world.getPlayerEntityByName(getOwnerName());
			if (player != null) setOwner(player);
		}
		
		return ownerCached;
	}
	
	/**
	 * Set the owner to the given player.
	 * <p>
	 * Also sets owner's BendingState FloatingBlock to this one.
	 * 
	 * @param owner
	 *            Owner to set to. Can set to null...
	 */
	public void setOwner(EntityPlayer owner) {
		this.ownerCached = owner;
		setOwnerName(owner == null ? "" : owner.getName());
		
		if (owner != null) {
			setOwnerCallback.accept(owner);
		}
		
	}
	
	/**
	 * Checks the cache's validity. If it is invalid, resets the cache and
	 * returns true.
	 * <p>
	 * Invalid conditions:
	 * <ul>
	 * <li>Cached owner is null
	 * <li>Cached owner is dead
	 * <li>Cached owner is not the correct owner
	 * <li>There is not supposed to be an owner
	 */
	private boolean isCacheInvalid() {
		if (ownerCached == null || ownerCached.isDead || !ownerCached.getName().equals(getOwnerName())
				|| getOwnerName() == null) {
			ownerCached = null;
			return true;
		}
		return false;
	}
	
}
