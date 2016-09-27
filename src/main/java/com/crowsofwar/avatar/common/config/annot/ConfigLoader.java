package com.crowsofwar.avatar.common.config.annot;

import java.lang.reflect.Field;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigLoader {
	
	public static void load(Object obj) {
		
		try {
			
			Class<?> cls = obj.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				
				if (field.getAnnotation(Load.class) != null) {
					System.out.println("Should load " + field.getName());
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
