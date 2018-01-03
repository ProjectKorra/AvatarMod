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
package com.crowsofwar.avatar.common.data;

import net.minecraft.util.math.BlockPos;

/**
 * @author CrowsOfWar
 */
public class TemporaryWaterLocation {

	private final AvatarWorldData data;
	private final BlockPos pos;
	private final int dimension;
	private int ticks;

	public TemporaryWaterLocation(AvatarWorldData data, BlockPos pos, int dimension, int ticks) {
		this.data = data;
		this.pos = pos;
		this.dimension = dimension;
		this.ticks = ticks;
	}

	public AvatarWorldData getData() {
		return data;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDimension() {
		return dimension;
	}

	public int getTicks() {
		return ticks;
	}

	public void decrementTicks() {
		this.ticks--;
		data.setDirty(true);
	}

}
