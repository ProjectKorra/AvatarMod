package com.crowsofwar.gorecore.config.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.gorecore.config.Animal;

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
	SET(Set.class),
	ANIMAL(Animal.class),
	STRING(String.class),
	FLOAT(Float.class);
	
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
			supers.addAll(allSuperclasses(cls));
			
			for (Class sup : supers) {
				if (classToType.containsKey(sup)) {
					return classToType.get(sup);
				}
			}
			throw new ConversionException("No type for class " + cls);
			
		}
		return classToType.get(cls);
	}
	
	/**
	 * Finds if there is a Type for the given class.
	 */
	public static boolean exists(Class<?> cls) {
		if (!classToType.containsKey(cls)) {
			// Try to find the correct superclass/interfaces for the object
			// Look through all of those, see if there is a type for one of them
			
			List<Class> supers = new ArrayList<>(Arrays.asList(cls.getInterfaces()));
			supers.addAll(allSuperclasses(cls));
			
			for (Class sup : supers) {
				if (classToType.containsKey(sup)) {
					return true;
				}
			}
			return false;
			
		}
		return true;
	}
	
	/**
	 * Returns all superclasses of the given object, not including Object.class
	 */
	private static List<Class> allSuperclasses(Class<?> cls) {
		List<Class> out = new ArrayList<>();
		Class sup = cls;
		while ((sup = sup.getSuperclass()) != Object.class) {
			out.add(sup);
		}
		return out;
	}
	
	static {
		classToType = new HashMap<>();
		for (Type t : values()) {
			classToType.put(t.cls, t);
		}
	}
	
}
