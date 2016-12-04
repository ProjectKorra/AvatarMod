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

package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;

/**
 * An IEntityProperty which uses the entity's DataManager to synchronize between
 * server and client. TODO add more docs
 *
 * @param <T>
 *            The type of object stored in the DataManager.
 */
public final class EntityPropertyDataManager<T> implements IEntityProperty<T> {
	
	private final Entity entity;
	private final EntityDataManager dataManager;
	private final DataParameter<T> parameter;
	
	/**
	 * Creates a new EntityProperty using the EntityDataManager for a specific
	 * entity.
	 * 
	 * @param entity
	 *            The instance of the entity
	 * @param clazz
	 *            The class of the entity
	 * @param serializer
	 *            The DataSerializer which will be used to send the value over
	 *            the network
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
	 *            The DataParameter which is used to store the entity property.
	 *            Note: Is also registered to the EntityDataManager.
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
