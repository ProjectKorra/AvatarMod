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

package com.crowsofwar.gorecore.config.convert;

import com.crowsofwar.gorecore.config.Animal;

import java.util.*;

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

	static {
		classToType = new HashMap<>();
		for (Type t : values()) {
			classToType.put(t.cls, t);
		}
	}

	private final Class<?> cls;

	Type(Class<?> cls) {
		this.cls = cls;
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

	/**
	 * Returns an ID for this type, which should be a multiple of 2, depending
	 * on the ordinal.
	 */
	public int id() {
		return (int) Math.pow(2, ordinal() - 1);
	}

}
