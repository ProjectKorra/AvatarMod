package com.crowsofwar.avatar.common.entityproperty;

import com.crowsofwar.avatar.common.util.AvBlockPos;

import net.minecraft.entity.Entity;

public class EntityPropertyBlockPos extends EntityPropertyDatawatcher<AvBlockPos> {
	
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
	protected void sendToDataWatcher(AvBlockPos value) {
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
	protected AvBlockPos retrieveFromDataWatcher() {
		if (dataWatcher.getWatchableObjectByte(index + 3) == 1) {
			return new AvBlockPos(dataWatcher.getWatchableObjectInt(index), dataWatcher.getWatchableObjectInt(index + 1),
					dataWatcher.getWatchableObjectInt(index + 2));
		} else {
			return null;
		}
	}
	
	@Override
	protected AvBlockPos createValue() {
		return null;
	}
	
}
