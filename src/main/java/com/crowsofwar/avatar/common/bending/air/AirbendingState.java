package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class AirbendingState extends BendingState {
	
	public AirbendingState(AvatarPlayerData data) {
		super(data);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_AIRBENDING;
	}
	
}
