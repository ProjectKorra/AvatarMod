package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vector;

public class EntityPropertyVector extends EntityPropertyDatawatcher<Vector> {
	
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
	protected void sendToDataWatcher(Vector value) {
		dataWatcher.updateObject(index, (float) value.xCoord);
		dataWatcher.updateObject(index + 1, (float) value.yCoord);
		dataWatcher.updateObject(index + 2, (float) value.zCoord);
	}
	
	@Override
	protected Vector retrieveFromDataWatcher() {
		return new Vector(dataWatcher.getWatchableObjectFloat(index), dataWatcher.getWatchableObjectFloat(index + 1),
				dataWatcher.getWatchableObjectFloat(index + 2));
	}
	
	@Override
	protected Vector createValue() {
		return new Vector(0, 0, 0);
	}
	
}
