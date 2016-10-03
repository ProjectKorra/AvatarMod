package com.crowsofwar.avatar.common.config.annot;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigLoader {
	
	private static Map<String, ?> loadMap(String path) {
		
		try {
			
			String contents = "";
			
			File file = new File("config/" + path);
			file.createNewFile();
			
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, ?> map = (Map) yaml.load(contents);
			
			return map;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void load(Object obj, String path) {
		load(obj, loadMap(path));
	}
	
	public static void load(Object obj, Map<String, ?> data) {
		
		try {
			
			Class<?> cls = obj.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				
				if (field.getAnnotation(Load.class) != null) {
					System.out.println("Should load " + field.getName());
					// Should load this field
					
					HasCustomLoader loaderAnnotation = field.getType().getAnnotation(HasCustomLoader.class);
					
					Object fromData = data.get(field.getName());
					Object instance;
					
					if (fromData instanceof Map<?, ?> && !field.getType().isAssignableFrom(Map.class)) {
						instance = field.getType().newInstance();
						if (loaderAnnotation.loadMarkedFields()) load(instance, (Map) fromData);
					} else {
						instance = fromData;
					}
					
					if (loaderAnnotation != null)
						loaderAnnotation.loaderClass().newInstance().load(null, instance);
					
					field.set(obj, instance);
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// TODO Implement
	/**
	 * Return a yaml representation of that object.
	 */
	public static String yaml(Object obj) {
		return null;
	}
	
}
