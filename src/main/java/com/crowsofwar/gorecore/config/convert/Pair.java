package com.crowsofwar.gorecore.config.convert;

/**
 * Used as a key in a map. Stores two Types.
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
		return new Pair(Type.of(clsA), Type.of(clsB));
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
