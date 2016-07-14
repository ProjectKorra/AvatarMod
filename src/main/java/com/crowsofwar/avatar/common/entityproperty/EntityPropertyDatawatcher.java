package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.DataWatcher;

public abstract class EntityPropertyDatawatcher<T> implements IEntityProperty<T> {
	
	protected final DataWatcher dataWatcher;
	protected final int index;
	private T internalValue;
	
	public EntityPropertyDatawatcher(DataWatcher dataWatcher, int dataWatcherIndex) {
		this.dataWatcher = dataWatcher;
		this.index = dataWatcherIndex;
	}
	
	@Override
	public T getValue() {
		if (dataWatcher.hasChanges()) internalValue = retrieveFromDataWatcher();
		return internalValue;
	}

	@Override
	public void setValue(T value) {
		this.internalValue = value;
	}
	
	public void sync() {
		sendToDataWatcher(internalValue);
	}
	
	public abstract void addToDataWatcher();
	protected abstract void sendToDataWatcher(T value);
	protected abstract T retrieveFromDataWatcher();
	
}
