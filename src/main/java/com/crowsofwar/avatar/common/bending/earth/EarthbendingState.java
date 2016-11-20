package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class EarthbendingState extends BendingState {
	
	private EntityFloatingBlock pickupBlock;
	
	public EarthbendingState(AvatarPlayerData data) {
		super(data);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
	public EntityFloatingBlock getPickupBlock() {
		if (pickupBlock != null && pickupBlock.isDead) {
			pickupBlock = null;
			save();
		}
		return pickupBlock;
	}
	
	public void setPickupBlock(EntityFloatingBlock pickupBlock) {
		this.pickupBlock = pickupBlock;
		save();
	}
	
	public boolean isHoldingBlock() {
		return getPickupBlock() != null;
	}
	
	public void dropBlock() {
		setPickupBlock(null);
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(pickupBlock == null ? -1 : pickupBlock.getID());
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		int id = buf.readInt();
		pickupBlock = id == -1 ? null : EntityFloatingBlock.getFromID(data.getPlayerEntity().worldObj, id);
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_EARTHBENDING;
	}
	
}
