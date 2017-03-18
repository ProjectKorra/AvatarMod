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
package com.crowsofwar.avatar.common.data.ctx;

import java.util.List;
import java.util.UUID;

import com.crowsofwar.gorecore.util.AccountUUIDs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Stores information about a {@link Bender} so that he/she can be found later.
 * 
 * @author CrowsOfWar
 */
public class BenderInfo {
	
	private final boolean player;
	private final UUID id;
	
	/**
	 * Creates bender info with null info, should only be used by NoBenderInfo
	 */
	protected BenderInfo() {
		player = false;
		id = null;
	}
	
	public BenderInfo(EntityLivingBase entity) {
		this(Bender.create(entity));
	}
	
	public BenderInfo(Bender bender) {
		player = bender.isPlayer();
		if (player) {
			id = AccountUUIDs.getId(bender.getName()).getUUID();
		} else {
			id = bender.getEntity().getPersistentID();
		}
	}
	
	public BenderInfo(boolean player, UUID id) {
		this.player = player;
		this.id = id;
	}
	
	public boolean isPlayer() {
		return player;
	}
	
	public UUID getId() {
		return id;
	}
	
	public Bender find(World world) {
		if (player) {
			return new PlayerBender(AccountUUIDs.findEntityFromUUID(world, id));
		} else {
			List<Entity> entities = world.loadedEntityList;
			for (Entity entity : entities) {
				if (entity.getPersistentID().equals(id)) {
					return (Bender) entity;
				}
			}
			return null;
		}
	}
	
	/**
	 * Writes to the NBT tag. Values are written directly onto the NBT.
	 */
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setBoolean("Player", player);
		nbt.setUniqueId("Id", id);
	}
	
	public static BenderInfo readFromNbt(NBTTagCompound nbt) {
		return new BenderInfo(nbt.getBoolean("Player"), nbt.getUniqueId("Id"));
	}
	
}
