package com.crowsofwar.avatar.common.bending;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class FirebendingState implements IBendingState {

	private int fireArcId;
	
	public FirebendingState() {
		fireArcId = -1;
	}
	
	public int getFireArcId() {
		return fireArcId;
	}
	
	public boolean isManipulatingFire() {
		return fireArcId != -1;
	}
	
	public void setFireArcId(int id) {
		fireArcId = id;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Implement saving fire arcs
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fireArcId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fireArcId = buf.readInt();
	}

	@Override
	public int getId() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
}
