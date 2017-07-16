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
package com.crowsofwar.avatar.common.data.ctx;

import com.crowsofwar.avatar.common.data.BenderInfoPlayer;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static sun.audio.AudioPlayer.player;

/**
 * Stores information about a {@link Bender} so that he/she can be found later.
 * 
 * @author CrowsOfWar
 */
public abstract class BenderInfo {
	
	/**
	 * Creates bender info with null info, should only be used by NoBenderInfo
	 */
	protected BenderInfo() {
	}

	public abstract boolean isPlayer();
	
	@Nullable
	public abstract UUID getId();

	@Nullable
	public abstract Bender find(World world);

	/**
	 * Gets a String to help identify which type of BenderInfo this is when being read from NBT.
	 */
	protected abstract String getNbtTag();

	public void writeToNbt(NBTTagCompound nbt) {
		
	}

	protected abstract void write(NBTTagCompound nbt);

	public static BenderInfo readFromNbt(NBTTagCompound nbt) {
		if (nbt.getBoolean("Player")) {
			return new BenderInfoPlayer(nbt.getUniqueId("Id"));
		} else if (nbt.getBoolean("None")) {
			return new NoBenderInfo();
		} else {
			return new BenderInfoMob(nbt.getUniqueId("Mob"));
		}
	}

	/**
	 * Writes to the NBT tag. Values are written directly onto the NBT.
	 */
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setBoolean("Player", player);
		nbt.setUniqueId("Id", id == null ? new UUID(0, 0) : id);
	}
	
	public static BenderInfo readFromNbt(NBTTagCompound nbt) {
		UUID id = nbt.getUniqueId("Id");
		id = id.getLeastSignificantBits() == 0 && id.getMostSignificantBits() == 0 ? null : id;
		return new BenderInfo(nbt.getBoolean("Player"), id);
	}
	
}
