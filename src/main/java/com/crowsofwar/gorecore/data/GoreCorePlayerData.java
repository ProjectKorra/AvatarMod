package com.crowsofwar.gorecore.data;

import java.util.UUID;

import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.MapUser;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public abstract class GoreCorePlayerData implements GoreCoreNBTInterfaces.ReadableWritable {
	
	public static final MapUser<UUID, GoreCorePlayerData> MAP_USER = new MapUser<UUID, GoreCorePlayerData>() {
		@Override
		public UUID createK(NBTTagCompound nbt, Object[] constructArgsK) {
			return GoreCoreNBTUtil.readUUIDFromNBT(nbt, "KeyUUID");
		}
		
		@Override
		public GoreCorePlayerData createV(NBTTagCompound nbt, UUID key, Object[] constructArgsV) {
			try {
				GoreCorePlayerData data = ((Class<? extends GoreCorePlayerData>) constructArgsV[0])
						.getConstructor(GoreCoreDataSaver.class, UUID.class, EntityPlayer.class)
						.newInstance(constructArgsV[1], key, null);
				data.readFromNBT(nbt);
				return data;
			} catch (Exception e) {
				FMLLog.severe("GoreCore> An error occured while creating new player data!");
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public void writeK(NBTTagCompound nbt, UUID obj) {
			GoreCoreNBTUtil.writeUUIDToNBT(nbt, "KeyUUID", obj);
		}
		
		@Override
		public void writeV(NBTTagCompound nbt, GoreCorePlayerData obj) {
			obj.writeToNBT(nbt);
		}
	};
	
	protected UUID playerID;
	protected GoreCoreDataSaver dataSaver;
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
	 * Please note, the subclass must create a constructor identical to this one, passing the
	 * arguments to <code>super</code>. This is because GoreCore player data is created with
	 * reflection.
	 * 
	 * @param dataSaver
	 *            Where data is saved to
	 * @param playerID
	 *            The account UUID of the player this player-data is for
	 * @param playerEntity
	 *            The player entity. May be null.
	 */
	public GoreCorePlayerData(GoreCoreDataSaver dataSaver, UUID playerID, EntityPlayer playerEntity) {
		construct(dataSaver, playerID, playerEntity);
	}
	
	/**
	 * Called from constructor to initialize data. Override to change constructor.
	 */
	protected void construct(GoreCoreDataSaver dataSaver, UUID playerID, EntityPlayer playerEntity) {
		if (dataSaver == null)
			FMLLog.severe("GoreCore> Player data was created with a null dataSaver - this is a bug! Debug:");
		if (playerID == null)
			FMLLog.severe("GoreCore> Player data was created with a null playerID - this is a bug! Debug:");
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
		this.playerID = GoreCoreNBTUtil.readUUIDFromNBT(nbt, "PlayerID");
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
	 * Returns whether this player data should be de-cached on a client-side Player Data Cache.
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
		this.playerEntity = player;
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
