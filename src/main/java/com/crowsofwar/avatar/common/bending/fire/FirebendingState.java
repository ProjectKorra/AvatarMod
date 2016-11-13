package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class FirebendingState extends BendingState {
	
	private EntityFireArc fireArc;
	private AvatarPlayerData data;
	private boolean isFlamethrowing;
	
	public FirebendingState(AvatarPlayerData data) {
		fireArc = null;
		this.data = data;
	}
	
	public int getFireArcId() {
		return fireArc == null ? -1 : fireArc.getId();
	}
	
	public EntityFireArc getFireArc() {
		return fireArc;
	}
	
	public boolean isManipulatingFire() {
		return fireArc != null;
	}
	
	public void setFireArc(EntityFireArc arc) {
		fireArc = arc;
	}
	
	public void setNoFireArc() {
		setFireArc(null);
	}
	
	public boolean isFlamethrowing() {
		return isFlamethrowing;
	}
	
	public void setFlamethrowing(boolean flamethrowing) {
		this.isFlamethrowing = flamethrowing;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		setFlamethrowing(nbt.getBoolean("Flamethrowing"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("Flamethrowing", isFlamethrowing());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(getFireArcId());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		fireArc = EntityFireArc.findFromId(data.getState().getPlayerEntity().worldObj, buf.readInt());
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
}
