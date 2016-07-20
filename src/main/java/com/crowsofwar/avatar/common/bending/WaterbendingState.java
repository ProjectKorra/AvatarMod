package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class WaterbendingState implements IBendingState {

	private final AvatarPlayerData data;
	
	public WaterbendingState(AvatarPlayerData data) {
		this.data = data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
