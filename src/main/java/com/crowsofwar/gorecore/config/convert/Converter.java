package com.crowsofwar.gorecore.config.convert;

/**
 * Converts an object of one type to an object of another type
 * 
 * @param F
 *            From type
 * @param T
 *            To type
 * 
 * @author CrowsOfWar
 */
public interface Converter<F, T> {
	
	T convert(F obj);
	
}
