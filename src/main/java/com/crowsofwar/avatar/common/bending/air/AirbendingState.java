package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class AirbendingState implements IBendingState {
	
	public AirbendingState() {
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_AIRBENDING;
	}
	
}
