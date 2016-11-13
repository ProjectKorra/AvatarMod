package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class WaterbendingState extends BendingState {
	
	private EntityWaterArc waterArc;
	
	public WaterbendingState(AvatarPlayerData data) {
		super(data);
		this.waterArc = null;
	}
	
	public EntityWaterArc getWaterArc() {
		return waterArc;
	}
	
	public int getWaterArcId() {
		return waterArc == null ? -1 : waterArc.getId();
	}
	
	public void setWaterArc(EntityWaterArc waterArc) {
		this.waterArc = waterArc;
	}
	
	public boolean isBendingWater() {
		return waterArc != null;
	}
	
	public void releaseWater() {
		this.waterArc = null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(getWaterArcId());
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		World world = data.getState().getPlayerEntity().worldObj;
		int id = buf.readInt();
		EntityWaterArc waterArc = null;
		if (id > -1) {
			waterArc = EntityWaterArc.findFromId(world, id);
			if (waterArc == null) AvatarLog.warn("WaterbendingState- Couldn't find water arc with ID " + id);
		}
		setWaterArc(waterArc);
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_WATERBENDING;
	}
	
}
