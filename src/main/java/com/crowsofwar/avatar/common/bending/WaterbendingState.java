package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class WaterbendingState implements IBendingState {

	private final AvatarPlayerData data;
	
	private EntityWaterArc waterArc;
	
	public WaterbendingState(AvatarPlayerData data) {
		this.data = data;
		this.waterArc = null;
	}
	
	public EntityWaterArc getWaterArc() {
		return waterArc;
	}
	
	public int getWaterArcId() {
		return waterArc.getId();
	}
	
	public void setWaterArc(EntityWaterArc waterArc) {
		this.waterArc = waterArc;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(getWaterArcId());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		World world = data.getState().getPlayerEntity().worldObj;
		int id = buf.readInt();
		EntityWaterArc waterArc = EntityWaterArc.findFromId(world, id);
		if (waterArc == null) AvatarLog.warn("WaterbendingState- Couldn't find water arc with ID " + id);
		setWaterArc(waterArc);
	}

	@Override
	public int getId() {
		return BendingManager.BENDINGID_WATERBENDING;
	}

}
