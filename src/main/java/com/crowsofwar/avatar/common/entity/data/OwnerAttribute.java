package com.crowsofwar.avatar.common.entity.data;

import static com.crowsofwar.avatar.common.bending.BendingType.EARTHBENDING;

import com.crowsofwar.avatar.common.bending.earth.EarthbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
	
	private EntityPlayer ownerCached;
	
	public OwnerAttribute(Entity entity, Class<? extends Entity> cls) {
		this.entity = entity;
		this.sync = EntityDataManager.createKey(cls, DataSerializers.STRING);
		this.world = entity.worldObj;
	}
	
	public void save(NBTTagCompound nbt) {
		
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
			EarthbendingState state = (EarthbendingState) AvatarPlayerData.fetcher().fetchPerformance(owner)
					.getBendingState(EARTHBENDING.id());
			state.setPickupBlock(entity);
		}
		
		System.out.println("Set owner to " + owner);
	}
	
}
