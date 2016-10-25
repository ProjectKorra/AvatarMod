package com.crowsofwar.gorecore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the type has a {@link CustomObjectLoader} which will be used
 * when the an object is being loaded from config.
 * 
 * @author CrowsOfWar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasCustomLoader {
	
	/**
	 * Get the class of the loader, which is called to actually load from
	 * configuration.
	 * <p>
	 * Since we can't use lambdas in members, anything using this will create a
	 * new instance of that CustomObjectLoader (reflection).
	 */
	Class<? extends CustomObjectLoader> loaderClass();
	
	/**
	 * Whether to proceed to load any fields marked with {@link Load}, then
	 * leave the rest to the {@link #loaderClass() custom loader}. If marked
	 * false, will only load according the loader class.
	 */
	boolean loadMarkedFields() default true;
	
}
