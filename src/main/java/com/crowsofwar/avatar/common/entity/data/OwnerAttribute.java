package com.crowsofwar.avatar.common.entity.data;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;

/**
 * An object which is designed to be used with an entity. Manages a synced owner
 * (player entity), storing it, and retrieving it.
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
		setOwnerName(nbt.getString("Owner"));
		getOwner(); // Look up owner in world
	}
	
	public void load(NBTTagCompound nbt) {
		nbt.setString("Owner", getOwnerName());
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
		
		if (!world.isRemote && ownerCached == null && getOwnerName() != null) {
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
		
		System.out.println("Set owner to " + owner);
	}
	
}
