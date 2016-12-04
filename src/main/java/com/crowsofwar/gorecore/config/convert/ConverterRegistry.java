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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all {@link Converter converters} by keeping them in memory.
 * Internally uses a Map using {@link Pair} as the key.
 * 
 * @author CrowsOfWar
 */
public class ConverterRegistry {
	
	private static final Map<Pair, Converter> converters = new HashMap<>();
	
	public static <F, T> void addConverter(Class<F> from, Class<T> to, Converter<F, T> convert) {
		Pair pair = Pair.of(from, to);
		converters.put(pair, convert);
	}
	
	public static <F, T> Converter<F, T> getConverter(Class<F> from, Class<T> to) {
		Pair pair = Pair.of(from, to);
		return converters.get(pair);
	}
	
	/**
	 * Returns whether there is a converter to convert.
	 */
	public static boolean isConverter(Class<?> from, Class<?> to) {
		if (!Pair.exists(from, to)) return false;
		Pair pair = Pair.of(from, to);
		return converters.containsKey(pair);
	}
	
	public static void addDefaultConverters() {
		addConverter(Integer.class, Double.class, integer -> integer.doubleValue());
		addConverter(Double.class, Integer.class, dubbl -> dubbl.intValue());
		addConverter(Integer.class, String.class, integer -> integer + "");
		addConverter(Double.class, String.class, dubbl -> dubbl + "");
		addConverter(Integer.class, Float.class, integer -> integer.floatValue());
		addConverter(Float.class, Integer.class, floatt -> floatt.intValue());
		addConverter(Double.class, Float.class, dubbl -> dubbl.floatValue());
		addConverter(Float.class, Double.class, floatt -> floatt.doubleValue());
	}
	
}
