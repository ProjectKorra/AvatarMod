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

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BenderInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class NoBenderInfo extends BenderInfo {

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public UUID getId() {
		return null;
	}

	@Override
	public Bender find(World world) {
		return null;
	}

	@Override
	public void writeToNbt(NBTTagCompound nbt) {
	}

}
