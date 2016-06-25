package com.maxandnoah.avatar.common.bending;

import java.util.Random;

import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class EarthbendingState implements IBendingState {
	
	private AvatarPlayerData data;
	private EntityFloatingBlock pickupBlock;
	
	public EarthbendingState(AvatarPlayerData data) {
		this.data = data;
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
		if (pickupBlock != null && pickupBlock.isDead) pickupBlock = null;
		return pickupBlock;
	}

	public void setPickupBlock(EntityFloatingBlock pickupBlock) {
		this.pickupBlock = pickupBlock;
	}
	
	public boolean isHoldingBlock() {
		return getPickupBlock() != null;
	}
	
	public void dropBlock() {
		pickupBlock = null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
//		int i = (int) ((new Random()).nextDouble() * 10);
//		System.out.println("EBS- Writing int: " + i);;
//		buf.writeInt(i);
		buf.writeInt(pickupBlock == null ? -1 : pickupBlock.getID());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
//		System.out.println("EBS- Read int: " + buf.readInt());
		int id = buf.readInt();
		pickupBlock = id == -1 ? null : EntityFloatingBlock.getFromID(data.getState().getPlayerEntity().worldObj, id);
	}

}
