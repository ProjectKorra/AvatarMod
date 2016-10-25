package com.crowsofwar.gorecore.config.convert;

/**
 * A pair of 2 {@link Type types}. Intended for use as a key in a map -
 * generally useless otherwise.
 * 
 * @author CrowsOfWar
 */
public class Pair {
	
	private final Type typeA, typeB;
	
	private Pair(Type typeA, Type typeB) {
		this.typeA = typeA;
		this.typeB = typeB;
	}
	
	/**
	 * Returns a TypePair which has the same classes as the given ones.
	 */
	public static Pair of(Class<?> clsA, Class<?> clsB) {
		if (clsA == null || clsB == null) {
			throw new ConversionException("Cannot create a pair with a null class");
		}
		return new Pair(Type.of(clsA), Type.of(clsB));
	}
	
	/**
	 * Returns whether a pair exists for the combination of those two types
	 */
	public static boolean exists(Class<?> clsA, Class<?> clsB) {
		return Type.exists(clsA) && Type.exists(clsB);
	}
	
	@Override
	public int hashCode() {
		return typeA.id() + typeB.id(); // is unique because ids are exps. of 2
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Pair)) return false;
		Pair pair = (Pair) obj;
		return pair.typeA == this.typeA && pair.typeB == this.typeB;
	}
	
}
