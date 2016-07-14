package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class EntityPropertyVector extends EntityPropertyDatawatcher<Vec3> {
	
	public EntityPropertyVector(Entity entity, DataWatcher dataWatcher, int dataWatcherIndex) {
		super(entity, dataWatcher, dataWatcherIndex);
	}
	
	@Override
	public void addToDataWatcher() {
		dataWatcher.addObject(index, 0f);
		dataWatcher.addObject(index + 1, 0f);
		dataWatcher.addObject(index + 2, 0f);
	}
	
	@Override
	protected void sendToDataWatcher(Vec3 value) {
		dataWatcher.updateObject(index, (float) value.xCoord);
		dataWatcher.updateObject(index + 1, (float) value.yCoord);
		dataWatcher.updateObject(index + 2, (float) value.zCoord);
	}
	
	@Override
	protected Vec3 retrieveFromDataWatcher() {
		return Vec3.createVectorHelper(dataWatcher.getWatchableObjectFloat(index),
				dataWatcher.getWatchableObjectFloat(index + 1), dataWatcher.getWatchableObjectFloat(index + 2));
	}

	@Override
	protected Vec3 createValue() {
		return Vec3.createVectorHelper(0, 0, 0);
	}

}
