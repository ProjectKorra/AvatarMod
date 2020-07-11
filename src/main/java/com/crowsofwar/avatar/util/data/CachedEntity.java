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

package com.crowsofwar.avatar.util.data;

import com.crowsofwar.gorecore.util.AccountUUIDs;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Represents an entity which is stored by its UUID but also cached for
 * performance. Can also store no entity.
 *
 * @author CrowsOfWar
 */
public class CachedEntity<T extends Entity> {

	private T cachedEntity;
	private UUID entityId;

	public CachedEntity(@Nullable UUID id) {
		this.entityId = id;
	}

	private static UUID getId(Entity entity) {
		return entity instanceof EntityPlayer ? AccountUUIDs.getId(entity.getName()) : entity.getUniqueID();
	}

	public void readFromNbt(NBTTagCompound nbt) {
		entityId = nbt.getBoolean("NoEntity") ? null : nbt.getUniqueId("EntityUuid");
	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setBoolean("NoEntity", entityId == null);
		if (entityId != null) {
			nbt.setUniqueId("EntityUuid", entityId);
		}
	}

	public void fromBytes(ByteBuf buf) {
		entityId = buf.readBoolean() ? null : new UUID(buf.readLong(), buf.readLong());
	}

	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(entityId == null);
		if (entityId != null) {
			buf.writeLong(entityId.getMostSignificantBits());
			buf.writeLong(entityId.getLeastSignificantBits());
		}
	}

	@Nullable
	public UUID getEntityId() {
		return entityId;
	}

	public void setEntityId(@Nullable UUID entityId) {
		this.entityId = entityId;
	}

	/**
	 * Finds the entity through the cache or searching for it.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public T getEntity(World world) {
		if (isCacheInvalid()) {
			List<Entity> list = world.getEntities(Entity.class, entity -> getId(entity).equals(entityId));
			cachedEntity = list.isEmpty() ? null : (T) list.get(0);
		}
		return cachedEntity;
	}

	public void setEntity(@Nullable T entity) {
		cachedEntity = entity;
		entityId = entity == null ? null : getId(entity);
	}

	/**
	 * Checks whether the cached entity is invalid (null or dead). If so, sets
	 * to null and returns true. Else returns false.
	 *
	 * @return whether cache is invalid; if true the cached entity is null
	 */
	private boolean isCacheInvalid() {
		return cachedEntity == null || cachedEntity.isDead || cachedEntity.getUniqueID() !=
				entityId;
	}

}
