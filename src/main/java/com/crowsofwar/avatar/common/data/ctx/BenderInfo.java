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
	 * Gets the type of this BenderInfo (according to class hierarchy) to be used in NBT compounds
	 */
	private String getType() {
		// this isn't an instance method since it would be a bit overcomplicated for something like this
		// Only using getType() wouldn't work for static method readFromNbt, which means a registry would be needed
		// ... which isn't necessary if BenderInfo only has 2-3 subclasses and will not add more in the future
		if (this instanceof BenderInfoPlayer) {
			return "Player";
		}
		if (this instanceof BenderInfoEntity) {
			return "Entity";
		}
		return "None";
	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setString("Type", getType());
		if (getId() != null) {
			nbt.setUniqueId("Id", getId());
		}
	}

	public static BenderInfo readFromNbt(NBTTagCompound nbt) {
		String type = nbt.getString("Type");
		UUID id = nbt.getUniqueId("Id");
		if (type.equals("Player")) {
			return new BenderInfoPlayer(id);
		} else if (type.equals("Entity")) {
			return new BenderInfoEntity(id);
		} else {
			return new NoBenderInfo();
		}
	}

}
