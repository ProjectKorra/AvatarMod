package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class FirebendingState implements IBendingState {
	
	private EntityFireArc fireArc;
	private AvatarPlayerData data;
	// TODO refactor flamethrower code- this is temporary
	private int timeLeftFlamethrowing;
	
	public FirebendingState(AvatarPlayerData data) {
		fireArc = null;
		this.data = data;
		this.timeLeftFlamethrowing = 0;
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
	
	public int getTicksLeftFlamethrowing() {
		return timeLeftFlamethrowing;
	}
	
	public boolean isFlamethrowing() {
		return timeLeftFlamethrowing > 0;
	}
	
	public void setNotFlamethrowing() {
		timeLeftFlamethrowing = 0;
	}
	
	public void setFlamethrowing(int ticks) {
		timeLeftFlamethrowing = ticks;
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
		buf.writeInt(getFireArcId());
		buf.writeInt(timeLeftFlamethrowing);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		fireArc = EntityFireArc.findFromId(data.getState().getPlayerEntity().worldObj, buf.readInt());
		timeLeftFlamethrowing = buf.readInt();
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
}
