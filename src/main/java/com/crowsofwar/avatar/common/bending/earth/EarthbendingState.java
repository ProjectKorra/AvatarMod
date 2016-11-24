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

package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class EarthbendingState implements IBendingState {
	
	private AvatarPlayerData data;
	private EntityFloatingBlock pickupBlock;
	
	public EarthbendingState(AvatarPlayerData data) {
		this.data = data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
	public EntityFloatingBlock getPickupBlock() {
		if (pickupBlock != null && pickupBlock.isDead) {
			pickupBlock = null;
		}
		return pickupBlock;
	}
	
	public void setPickupBlock(EntityFloatingBlock pickupBlock) {
		this.pickupBlock = pickupBlock;
	}
	
	public boolean isHoldingBlock() {
		return getPickupBlock() != null;
	}
	
	public void dropBlock() {
		pickupBlock = null;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pickupBlock == null ? -1 : pickupBlock.getID());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int id = buf.readInt();
		pickupBlock = id == -1 ? null
				: EntityFloatingBlock.getFromID(data.getState().getPlayerEntity().worldObj, id);
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_EARTHBENDING;
	}
	
}
