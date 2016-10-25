package com.crowsofwar.gorecore.config.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes a type which can be converted. E.g. an integer.
 * <p>
 * Each type has a class associated with it; for example, the List type uses
 * <code>List.class</code>.
 * 
 * @author CrowsOfWar
 */
public enum Type {
	
	ERROR(null),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	LIST(List.class),
	SET(Set.class);
	
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
		if (!classToType.containsKey(cls)) {
			// Try to find the correct superclass/interfaces for the object
			// Look through all of those, see if there is a type for one of them
			
			List<Class> supers = new ArrayList<>(Arrays.asList(cls.getInterfaces()));
			supers.add(cls.getSuperclass());
			
			for (Class sup : supers) {
				if (classToType.containsKey(sup)) {
					return classToType.get(sup);
				}
			}
			throw new ConversionException("No type for class " + cls);
			
		}
		return classToType.get(cls);
	}
	
	static {
		classToType = new HashMap<>();
		for (Type t : values()) {
			classToType.put(t.cls, t);
		}
	}
	
}
