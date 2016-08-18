package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityPropertyVector extends EntityPropertyDatawatcher<Vec3d> {
	
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
	protected void sendToDataWatcher(Vec3d value) {
		dataWatcher.updateObject(index, (float) value.xCoord);
		dataWatcher.updateObject(index + 1, (float) value.yCoord);
		dataWatcher.updateObject(index + 2, (float) value.zCoord);
	}
	
	@Override
	protected Vec3d retrieveFromDataWatcher() {
		return Vec3d.createVectorHelper(dataWatcher.getWatchableObjectFloat(index), dataWatcher.getWatchableObjectFloat(index + 1),
				dataWatcher.getWatchableObjectFloat(index + 2));
	}
	
	@Override
	protected Vec3d createValue() {
		return Vec3d.createVectorHelper(0, 0, 0);
	}
	
}
