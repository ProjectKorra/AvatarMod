package com.crowsofwar.avatar.common.config.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the field should be loaded from the configuration file.
 * 
 * @author CrowsOfWar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Load {
	
}
