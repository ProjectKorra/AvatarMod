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

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingState;
import com.crowsofwar.avatar.common.data.CachedEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class WaterbendingState extends BendingState {
	
	private CachedEntity<EntityWaterBubble> waterBubble;
	
	public WaterbendingState(AvatarPlayerData data) {
		super(data);
		this.waterBubble = new CachedEntity<>(-1);
	}
	
	/**
	 * Gets the instance of EntityWaterBubble which the player is currently
	 * controlling, which is synced across client and server.
	 */
	public @Nullable EntityWaterBubble getBubble(World world) {
		return waterBubble.getEntity(world);
	}
	
	/**
	 * Sets a synced instance of EntityWaterBubble
	 */
	public void setBubble(@Nullable EntityWaterBubble bubble) {
		this.waterBubble.setEntity(bubble);
		save();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		waterBubble.readFromNBT(nestedCompound(nbt, "WaterBubble"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		waterBubble.writeToNBT(nestedCompound(nbt, "WaterBubble"));
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		waterBubble.toBytes(buf);
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		waterBubble.fromBytes(buf);
	}
	
	@Override
	public int getId() {
		return BendingManager.BENDINGID_WATERBENDING;
	}
	
}
