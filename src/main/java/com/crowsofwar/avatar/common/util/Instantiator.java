package com.crowsofwar.avatar.common.util;

public interface Instantiator<T> {
	
	/**
	 * Create new instance of T
	 * @param info Information, method specific
	 */
	public T createNew(Object[] info);
	
}
