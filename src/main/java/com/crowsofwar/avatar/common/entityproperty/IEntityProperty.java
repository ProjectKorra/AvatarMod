package com.crowsofwar.avatar.common.entityproperty;

public interface IEntityProperty<T> {
	
	T getValue();
	
	void setValue(T value);
	
}
