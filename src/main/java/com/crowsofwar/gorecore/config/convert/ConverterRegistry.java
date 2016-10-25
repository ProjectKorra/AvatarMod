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
	
}
