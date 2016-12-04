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
