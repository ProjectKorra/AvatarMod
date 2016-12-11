/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.findNestedCompound;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.data.CachedEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class WaterbendingState extends BendingState {
	
	private EntityWaterArc waterArc;
	private CachedEntity<EntityWaterBubble> waterBubble;
	
	public WaterbendingState(AvatarPlayerData data) {
		super(data);
		this.waterArc = null;
		this.waterBubble = new CachedEntity<>(-1);
	}
	
	public EntityWaterArc getWaterArc() {
		return waterArc;
	}
	
	public int getWaterArcId() {
		return waterArc == null ? -1 : waterArc.getId();
	}
	
	public void setWaterArc(EntityWaterArc waterArc) {
		this.waterArc = waterArc;
		save();
	}
	
	public boolean isBendingWater() {
		return waterArc != null;
	}
	
	public void releaseWater() {
		setWaterArc(null);
	}
	
	public EntityWaterBubble getBubble(World world) {
		return waterBubble.getEntity(world);
	}
	
	public void setBubble(EntityWaterBubble bubble) {
		this.waterBubble.setEntity(bubble);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		waterBubble.readFromNBT(findNestedCompound(nbt, "WaterBubble"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		waterBubble.writeToNBT(findNestedCompound(nbt, "WaterBubble"));
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(getWaterArcId());
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		World world = data.getPlayerEntity().worldObj;
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
