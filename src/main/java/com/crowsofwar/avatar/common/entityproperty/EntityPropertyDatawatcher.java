package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;

/**
 * An IEntityProperty which uses the entity's DataWatcher to synchronize between server and client.
 * How the object is stored/retrieved from DataWatcher depends on the subclass implementation. The
 * value must be sent to clients using {@link #sync()}.
 *
 * @param <T>
 *            The type of object stored in the DataWatcher.
 */
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
	
	/**
	 * Synchronize the data. On clients, it retrieves the latest data from the server; on server
	 * side, sends the value to server.
	 */
	public final void sync() {
		if (entity.worldObj.isRemote)
			setValue(retrieveFromDataWatcher());
		else
			sendToDataWatcher(internalValue);
	}
	
	/**
	 * Called to add necessary objects to the DataWatcher. Use the {@link #dataWatcher} and
	 * {@link #index} fields, calling {@link DataWatcher#addObject(int, Object)}.
	 */
	protected abstract void addToDataWatcher();
	
	/**
	 * Called to send the desired value to the DataWatcher. Use the {@link #dataWatcher} and
	 * {@link #index} fields, calling {@link DataWatcher#updateObject(int, Object)}.
	 * 
	 * @param value
	 */
	protected abstract void sendToDataWatcher(T value);
	
	/**
	 * Called to create a new object based on the DataWatcher's values. Use the {@link #dataWatcher}
	 * and {@link #index} fields, calling a getWatchableObject method.
	 */
	protected abstract T retrieveFromDataWatcher();
	
	/**
	 * Called to create a new object using its default values.
	 */
	protected abstract T createValue();
	
}
