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

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NoBenderInfo extends BenderInfo {
	
	private static UUID ID_ZERO = new UUID(0, 0);
	
	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public UUID getId() {
		return ID_ZERO;
	}
	
	@Override
	public Bender find(World world) {
		return null;
	}
	
	@Override
	public void writeToNbt(NBTTagCompound nbt) {}
	
}
