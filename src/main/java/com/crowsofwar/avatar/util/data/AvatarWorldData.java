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

import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.data.WorldDataPlayers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AvatarWorldData extends WorldDataPlayers<AvatarPlayerData> {

	public static final String WORLD_DATA_KEY = "Avatar";
	private int nextEntityId;

	private List<ScheduledDestroyBlock> scheduledDestroyBlocks;
	private List<TemporaryWaterLocation> temporaryWater;

	public AvatarWorldData() {
		super(WORLD_DATA_KEY);
		nextEntityId = 1;
		scheduledDestroyBlocks = new ArrayList<>();
		temporaryWater = new ArrayList<>();
	}

	public AvatarWorldData(String key) {
		this();
	}

	public static AvatarWorldData getDataFromWorld(World world) {
		if (world.isRemote) {
			throw new IllegalStateException("AvatarWorldData is designed to be used only on " +
					"server side");
		}

		return getDataForWorld(AvatarWorldData.class, WORLD_DATA_KEY, world, false);
	}

	@Override
	public Class<AvatarPlayerData> playerDataClass() {
		return AvatarPlayerData.class;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		nextEntityId = nbt.getInteger("NextEntityId");

		AvatarUtils.readList(scheduledDestroyBlocks, compound -> {

			BlockPos pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"),
					compound.getInteger("z"));
			return new ScheduledDestroyBlock(this, pos, compound.getInteger("Ticks"),
					compound.getBoolean("Drop"), compound.getInteger("Fortune"));

		}, nbt, "DestroyBlocks");

		AvatarUtils.readList(temporaryWater, c -> {

			BlockPos pos = new BlockPos(c.getInteger("x"), c.getInteger("y"), c.getInteger("z"));
			return new TemporaryWaterLocation(this, pos, c.getInteger("Dimension"), c.getInteger("Ticks"));

		}, nbt, "TemporaryWater");

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
			compound.setInteger("Fortune", sdb.fortune);
		}, nbt, "DestroyBlocks");
		AvatarUtils.writeList(temporaryWater, (c, water) -> {
			c.setInteger("x", water.getPos().getX());
			c.setInteger("y", water.getPos().getY());
			c.setInteger("z", water.getPos().getZ());
			c.setInteger("Ticks", water.getTicks());
			c.setInteger("Dimension", water.getDimension());
		}, nbt, "TemporaryWater");
		return nbt;
	}

	public int nextEntityId() {
		return ++nextEntityId;
	}

	public List<ScheduledDestroyBlock> getScheduledDestroyBlocks() {
		return scheduledDestroyBlocks;
	}

	public List<TemporaryWaterLocation> geTemporaryWaterLocations() {
		return temporaryWater;
	}

	public void addTemporaryWaterLocation(BlockPos pos) {
		temporaryWater.add(new TemporaryWaterLocation(this, pos, getWorld().provider.getDimension(), 15));
	}

}
