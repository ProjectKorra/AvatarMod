package com.crowsofwar.gorecore.config.convert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a type which can be converted. E.g. an integer.
 * 
 * @author CrowsOfWar
 */
public enum Type {
	
	ERROR(null),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	LIST(List.class);
	
	private static final Map<Class<?>, Type> classToType;
	private final Class<?> cls;
	
	private Type(Class<?> cls) {
		this.cls = cls;
	}
	
	/**
	 * Returns an ID for this type, which should be a multiple of 2, depending
	 * on the ordinal.
	 */
	public int id() {
		return (int) Math.pow(2, ordinal() - 1);
	}
	
	/**
	 * Finds an instance of Type which has the same class as the given one.
	 */
	public static Type of(Class<?> cls) {
		return classToType.get(cls);
	}
	
	static {
		classToType = new HashMap<>();
		for (Type t : values()) {
			classToType.put(t.cls, t);
		}
	}
	
}
