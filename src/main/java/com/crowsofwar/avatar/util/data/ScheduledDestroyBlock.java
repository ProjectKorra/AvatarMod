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
package com.crowsofwar.avatar.util.data;

import net.minecraft.util.math.BlockPos;

public class ScheduledDestroyBlock {

	final BlockPos pos;
	final boolean drop;
	final int fortune;
	private final AvatarWorldData data;
	int ticks;

	public ScheduledDestroyBlock(AvatarWorldData avatarWorldData, BlockPos pos, int ticks, boolean drop, int fortune) {
		data = avatarWorldData;
		this.pos = pos;
		this.ticks = ticks;
		this.fortune = fortune;
		this.drop = drop;
	}

	public int getTicks() {
		return ticks;
	}

	public void decrementTicks() {
		this.ticks--;
		data.setDirty(true);
	}

	public BlockPos getPos() {
		return pos;
	}

	public boolean isDrop() {
		return drop;
	}

	public int getFortune() {
		return fortune;
	}

}