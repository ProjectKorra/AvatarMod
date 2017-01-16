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

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.PlayerData;
import com.crowsofwar.gorecore.data.WorldDataPlayers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AvatarWorldData extends WorldDataPlayers<AvatarPlayerData> {
	
	public static final String WORLD_DATA_KEY = "Avatar";
	private int nextEntityId;
	
	private List<ScheduledDestroyBlock> scheduledDestroyBlocks;
	
	public AvatarWorldData() {
		super(WORLD_DATA_KEY);
		nextEntityId = 0;
		scheduledDestroyBlocks = new ArrayList<>();
	}
	
	public AvatarWorldData(String key) {
		this();
	}
	
	@Override
	public Class<? extends PlayerData> playerDataClass() {
		return AvatarPlayerData.class;
	}
	
	public static AvatarWorldData getDataFromWorld(World world) {
		return getDataForWorld(AvatarWorldData.class, WORLD_DATA_KEY, world, false);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		nextEntityId = nbt.getInteger("NextEntityId");
		AvatarUtils.readList(scheduledDestroyBlocks, compound -> {
			
			BlockPos pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"),
					compound.getInteger("z"));
			return new ScheduledDestroyBlock(pos, compound.getInteger("Ticks"), compound.getBoolean("Drop"));
			
		}, nbt, "DestroyBlocks");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("NextEntityId", nextEntityId);
		AvatarUtils.writeList(scheduledDestroyBlocks, (compound, sdb) -> {
			compound.setInteger("x", sdb.pos.getX());
			compound.setInteger("y", sdb.pos.getY());
			compound.setInteger("z", sdb.pos.getZ());
			compound.setInteger("Ticks", sdb.ticks);
			compound.setBoolean("Drop", sdb.drop);
		}, nbt, "DestroyBlocks");
		return nbt;
	}
	
	public int nextEntityId() {
		return ++nextEntityId;
	}
	
	public List<ScheduledDestroyBlock> getScheduledDestroyBlocks() {
		return scheduledDestroyBlocks;
	}
	
	public class ScheduledDestroyBlock {
		
		private final BlockPos pos;
		private final boolean drop;
		private int ticks;
		
		public ScheduledDestroyBlock(BlockPos pos, int ticks, boolean drop) {
			this.pos = pos;
			this.ticks = ticks;
			this.drop = drop;
		}
		
		public int getTicks() {
			return ticks;
		}
		
		public void decrementTicks() {
			this.ticks--;
			AvatarWorldData.this.setDirty(true);
		}
		
		public BlockPos getPos() {
			return pos;
		}
		
		public boolean isDrop() {
			return drop;
		}
		
	}
	
}
