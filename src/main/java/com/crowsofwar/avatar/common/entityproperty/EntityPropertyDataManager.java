package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;

/**
 * An IEntityProperty which uses the entity's DataManager to synchronize between server and client.
 * TODO add more docs
 *
 * @param <T>
 *            The type of object stored in the DataManager.
 */
public final class EntityPropertyDataManager<T> implements IEntityProperty<T> {
	
	private final Entity entity;
	private final EntityDataManager dataManager;
	private final DataParameter<T> parameter;
	
	/**
	 * Creates a new EntityProperty using the EntityDataManager for a specific entity.
	 * 
	 * @param entity
	 *            The instance of the entity
	 * @param clazz
	 *            The class of the entity
	 * @param serializer
	 *            The DataSerializer which will be used to send the value over the network
	 * @param startingValue
	 *            The initial value of your property
	 */
	public <E extends Entity> EntityPropertyDataManager(E entity, Class<E> clazz,
			DataSerializer<T> serializer, T startingValue) {
		this.entity = entity;
		this.dataManager = entity.getDataManager();
		this.parameter = EntityDataManager.createKey(clazz, serializer);
		this.dataManager.register(parameter, startingValue);
	}
	
	/**
	 * Create a new EntityProperty.
	 * 
	 * @param entity
	 *            The instance of the entity
	 * @param parameter
	 *            The DataParameter which is used to store the entity property. Note: Is also
	 *            registered to the EntityDataManager.
	 * @param startingValue
	 *            The initial value of the property.
	 */
	public EntityPropertyDataManager(Entity entity, DataParameter<T> parameter, T startingValue) {
		this.entity = entity;
		this.dataManager = entity.getDataManager();
		this.parameter = parameter;
		this.dataManager.register(parameter, startingValue);
	}
	
	@Override
	public T getValue() {
		return dataManager.get(parameter);
	}
	
	@Override
	public void setValue(T value) {
		dataManager.set(parameter, value);
	}
	
}
