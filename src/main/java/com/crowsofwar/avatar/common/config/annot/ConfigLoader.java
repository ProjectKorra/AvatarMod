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
	
	/**
	 * Load a Map containing the YAML configurations at that path.
	 * 
	 * @param path
	 *            Path starting at ".minecraft/config/"
	 */
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
	
	/**
	 * Populate the object's fields marked with with {@link Load} with data from the configuration
	 * file.
	 * <p>
	 * If fields are already set (i.e. not null), their current values will only be preserved if
	 * there is not entry in configuration.
	 * <p>
	 * If an object is being loaded, ConfigLoader will attempt to load that object the same way that
	 * <code>obj</code> is being loaded. If a {@link HasCustomLoader custom loader} is specified,
	 * ConfigLoader will call that loader to perform any additional modifications after loading
	 * the @Load fields.
	 * 
	 * @param obj
	 *            Object to load
	 * @param path
	 *            Path to the configuration file, from ".minecraft/config/"
	 */
	public static void load(Object obj, String path) {
		load(obj, loadMap(path));
	}
	
	/**
	 * Populate that object's fields marked with {@link Load} with data from the configuration map.
	 * 
	 * @param obj
	 *            Object to load
	 * @param data
	 *            Map containing configuration data
	 */
	private static void load(Object obj, Map<String, ?> data) {
		
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
					
					if (fromData == null) {
						
						if (field.get(obj) != null) {
							instance = field.get(obj);
						} else {
							throw new Exception(
									"No configured definition for " + field.getName() + ", no default value");
						}
						
					} else {
						if (fromData instanceof Map<?, ?> && !field.getType().isAssignableFrom(Map.class)) {
							instance = field.getType().newInstance();
							if (loaderAnnotation.loadMarkedFields()) load(instance, (Map) fromData);
						} else {
							instance = fromData;
						}
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
