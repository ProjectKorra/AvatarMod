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
		return BendingManager.BENDINGID_WATERBENDING;
	}

}
