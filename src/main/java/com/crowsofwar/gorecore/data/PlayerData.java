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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.*;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.MapUser;

import java.util.UUID;

public abstract class PlayerData implements GoreCoreNBTInterfaces.ReadableWritable {

	public static final MapUser<UUID, PlayerData> MAP_USER = new MapUser<UUID, PlayerData>() {
		@Override
		public UUID createK(NBTTagCompound nbt, Object[] constructArgsK) {
			return GoreCoreNBTUtil.readUUIDFromNBT(nbt, "KeyUUID");
		}

		@Override
		public PlayerData createV(NBTTagCompound nbt, UUID key, Object[] constructArgsV) {
			try {
				PlayerData data = ((Class<? extends PlayerData>) constructArgsV[0]).getConstructor(DataSaver.class, UUID.class, EntityPlayer.class)
								.newInstance((DataSaver) constructArgsV[1], key, null);
				data.readFromNBT(nbt);
				return data;
			} catch (Exception e) {
				GoreCore.LOGGER.error("An error occurred while creating new player data!");
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public void writeK(NBTTagCompound nbt, UUID obj) {
			GoreCoreNBTUtil.writeUUIDToNBT(nbt, "KeyUUID", obj);
		}

		@Override
		public void writeV(NBTTagCompound nbt, PlayerData obj) {
			obj.writeToNBT(nbt);
		}
	};

	protected UUID playerID;
	private DataSaver dataSaver;
	/**
	 * The player entity this player-data is attached to.
	 * <p>
	 * May be null on server.
	 * <p>
	 * Is not null {@link #shouldBeDecached() by default} on client.
	 */
	private EntityPlayer playerEntity;

	/**
	 * Creates new GC player data.
	 * <p>
	 * Please note, the subclass must get a constructor identical to this
	 * one, passing the arguments to <code>super</code>. This is because
	 * GoreCore player data is created with reflection.
	 *
	 * @param dataSaver    Where data is saved to
	 * @param playerID     The account UUID of the player this player-data is for
	 * @param playerEntity The player entity. May be null.
	 */
	public PlayerData(DataSaver dataSaver, UUID playerID, EntityPlayer playerEntity) {
		construct(dataSaver, playerID, playerEntity);
	}

	/**
	 * Called from constructor to initialize data. Override to change
	 * constructor.
	 */
	protected void construct(DataSaver dataSaver, UUID playerID, EntityPlayer playerEntity) {
		if (dataSaver == null) GoreCore.LOGGER.error("Player data was created with a null dataSaver - this is a bug! Debug:");
		if (playerID == null) GoreCore.LOGGER.error("Player data was created with a null playerID - this is a bug! Debug:");
		if (dataSaver == null || playerID == null) Thread.dumpStack();

		this.dataSaver = dataSaver;
		this.playerID = playerID;
		this.playerEntity = playerEntity;
	}

	protected void saveChanges() {
		dataSaver.saveChanges();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		playerID = GoreCoreNBTUtil.readUUIDFromNBT(nbt, "PlayerID");
		readPlayerDataFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		GoreCoreNBTUtil.writeUUIDToNBT(nbt, "PlayerID", playerID);
		writePlayerDataToNBT(nbt);
	}

	protected abstract void readPlayerDataFromNBT(NBTTagCompound nbt);

	protected abstract void writePlayerDataToNBT(NBTTagCompound nbt);

	public UUID getPlayerID() {
		return playerID;
	}

	/**
	 * Returns whether this player data should be de-cached on a client-side
	 * Player Data Cache.
	 * <p>
	 * This is only used by {@link PlayerDataFetcherClient}.
	 * <p>
	 * By default, returns true if the player entity is dead (unloaded).
	 */
	public boolean shouldBeDecached() {
		return playerEntity.isDead;
	}

	/**
	 * The player entity this player-data is attached to.
	 * <p>
	 * May be null on server.
	 * <p>
	 * Is not null {@link #shouldBeDecached() by default} on client.
	 */
	public EntityPlayer getPlayerEntity() {
		return playerEntity;
	}

	public void setPlayerEntity(EntityPlayer player) {
		playerEntity = player;
	}

	/**
	 * Get the world this player data is in.
	 * <p>
	 * Uses {@link #getPlayerEntity()} for the world. It may be null on servers.
	 */
	public World getWorld() {
		return getPlayerEntity() == null ? null : getPlayerEntity().getEntityWorld();
	}

}
