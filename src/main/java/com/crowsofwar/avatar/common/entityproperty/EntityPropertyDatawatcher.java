package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;

public abstract class EntityPropertyDatawatcher<T> implements IEntityProperty<T> {
	
	protected final Entity entity;
	protected final DataWatcher dataWatcher;
	protected final int index;
	private T internalValue;
	
	public EntityPropertyDatawatcher(Entity entity, DataWatcher dataWatcher, int dataWatcherIndex) {
		this.entity = entity;
		this.dataWatcher = dataWatcher;
		this.index = dataWatcherIndex;
		this.internalValue = createValue();
		this.addToDataWatcher();
	}
	
	@Override
	public final T getValue() {
		return internalValue;
	}

	@Override
	public final void setValue(T value) {
		this.internalValue = value;
	}
	
	public void sync() {
		if (entity.worldObj.isRemote)
			setValue(retrieveFromDataWatcher());
		else
			sendToDataWatcher(internalValue);
	}
	
	protected abstract void addToDataWatcher();
	protected abstract void sendToDataWatcher(T value);
	protected abstract T retrieveFromDataWatcher();
	protected abstract T createValue();
	
}
