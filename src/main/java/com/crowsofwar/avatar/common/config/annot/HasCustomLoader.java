package com.crowsofwar.avatar.common.config.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the type has a {@link CustomObjectLoader} which will be used when the an object is
 * being loaded from config.
 * 
 * @author CrowsOfWar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasCustomLoader {
	
	/**
	 * Since we can't use lambdas in members, it creates a new instance of that CustomObjectLoader.
	 */
	Class<? extends CustomObjectLoader> loaderClass();
	
	/**
	 * Whether to proceed to load any fields marked with {@link Load}, then leave the rest to the
	 * {@link #loaderClass() custom loader}.
	 */
	boolean loadMarkedFields() default true;
	
}
