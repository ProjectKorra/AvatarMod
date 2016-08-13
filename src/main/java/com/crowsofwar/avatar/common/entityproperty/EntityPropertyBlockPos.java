package com.crowsofwar.avatar.common.entityproperty;

import com.crowsofwar.avatar.common.util.BlockPos;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;

public class EntityPropertyBlockPos extends EntityPropertyDatawatcher<BlockPos> {
	
	public EntityPropertyBlockPos(Entity entity, DataWatcher dataWatcher, int dataWatcherIndex) {
		super(entity, dataWatcher, dataWatcherIndex);
	}
	
	@Override
	protected void addToDataWatcher() {
		dataWatcher.addObject(index, 0);
		dataWatcher.addObject(index + 1, 0);
		dataWatcher.addObject(index + 2, 0);
		dataWatcher.addObject(index + 3, (byte) 0);
	}
	
	@Override
	protected void sendToDataWatcher(BlockPos value) {
		if (value == null) {
			dataWatcher.updateObject(index + 3, (byte) 0);
		} else {
			dataWatcher.updateObject(index, value.x);
			dataWatcher.updateObject(index + 1, value.y);
			dataWatcher.updateObject(index + 2, value.z);
			dataWatcher.updateObject(index + 3, (byte) 1);
		}
	}
	
	@Override
	protected BlockPos retrieveFromDataWatcher() {
		if (dataWatcher.getWatchableObjectByte(index + 3) == 1) {
			return new BlockPos(dataWatcher.getWatchableObjectInt(index), dataWatcher.getWatchableObjectInt(index + 1),
					dataWatcher.getWatchableObjectInt(index + 2));
		} else {
			return null;
		}
	}
	
	@Override
	protected BlockPos createValue() {
		return null;
	}
	
}
