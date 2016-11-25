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

package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;

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
