package com.crowsofwar.gorecore.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import com.crowsofwar.gorecore.config.convert.ConverterRegistry;

import jline.internal.Nullable;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigLoader {
	
	private final String path;
	private Map<String, Object> values;
	
	private ConfigLoader(String path) {
		this.path = path;
	}
	
	/**
	 * Load a Map containing the YAML configurations at that path.
	 * 
	 * @param path
	 *            Path starting at ".minecraft/config/"
	 * 
	 * @throws ConfigurationException
	 *             when an error occurs while trying to read the file
	 */
	private static Map<String, Object> loadMap(String path) {
		
		try {
			
			String contents = "";
			
			File file = new File("config/" + path);
			file.createNewFile();
			
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, Object> map = (Map) yaml.load(contents);
			
			return map;
			
		} catch (IOException e) {
			throw new ConfigurationException.LoadingException(
					"Exception trying to load config file at config/" + path, e);
		} catch (ClassCastException e) {
			throw new ConfigurationException.UserMistake(
					"Invalid configuration file at config/" + path + ": not a map");
		} catch (Exception e) {
			
			// TODO use a logger
			System.err.println("Error while loading config at 'config/" + path + "':");
			throw e;
			
		}
		
	}
	
	/**
	 * Populate the object's fields marked with with {@link Load} with data from
	 * the configuration file.
	 * <p>
	 * If fields are already set (i.e. not null), their current values will only
	 * be preserved if there is not entry in configuration.
	 * <p>
	 * If an object is being loaded, ConfigLoader will attempt to load that
	 * object the same way that <code>obj</code> is being loaded. If a
	 * {@link HasCustomLoader custom loader} is specified, ConfigLoader will
	 * call that loader to perform any additional modifications after loading
	 * the @Load fields.
	 * 
	 * @param obj
	 *            Object to load
	 * @param path
	 *            Path to the configuration file, from ".minecraft/config/"
	 */
	public static void load(Object obj, String path) {
		ConfigLoader loader = new ConfigLoader(path);
		loader.values = loadMap(path);
		loader.load(obj.getClass(), obj, loader.values);
		loader.save();
	}
	
	/**
	 * Populate that object's fields marked with {@link Load} with data from the
	 * configuration map.
	 * 
	 * @param cls
	 *            Class to get fields from. Normally it would be
	 *            <code>obj.getClass()</code>.
	 * @param obj
	 *            Object to load
	 * @param data
	 *            Map containing configuration data
	 * 
	 * @throws ConfigurationException
	 */
	private void load(Class<?> cls, @Nullable Object obj, Map<String, ?> data) {
		
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			
			loadField(field, data, obj);
			
		}
		
	}
	
	/**
	 * Tries to load the field with the correct data.
	 * <p>
	 * If the field isn't marked with @Load, does nothing. Otherwise, will
	 * attempt to set the field's value (with reflection) to the data set in the
	 * map.
	 * 
	 * @param field
	 *            The field to load
	 * @param data
	 *            The map containing the data of what to load.
	 * @param obj
	 *            The object which contains the field
	 */
	private <T> void loadField(Field field, Map<String, ?> data, T obj) {
		
		Class<?> cls = field.getDeclaringClass();
		
		try {
			
			if (field.getAnnotation(Load.class) != null) {
				System.out.println("Should load " + field.getName());
				// Should load this field
				
				HasCustomLoader loaderAnnot = field.getType().getAnnotation(HasCustomLoader.class);
				CustomLoaderSettings loaderInfo = loaderAnnot == null ? new CustomLoaderSettings()
						: new CustomLoaderSettings(loaderAnnot);
				
				Object fromData = data.get(field.getName());
				Object setTo;
				
				if (fromData == null) {
					
					// Nothing present- try to load default value
					System.out.println(" -> Nothing present; trying default value");
					
					if (field.get(obj) != null) {
						
						setTo = field.get(obj);
						values.put(field.getName(), setTo);
						
						System.out.println(" -> Found default " + setTo);
						
					} else {
						throw new ConfigurationException.UserMistake(
								"No configured definition for " + field.getName() + ", no default value");
					}
					
				} else {
					
					// Value present in configuration.
					// Use the present value from map: fromData
					
					System.out.println(" -> Using from cfg.");
					
					Class<Object> from = (Class<Object>) fromData.getClass();
					Class<?> to = field.getType();
					
					// 3 possibilities. Done in this order:
					//
					// 1. from == to. So it is the right type already
					// 2. There is a converter to convert from->to.
					// 3. from is a map. to is not. This means, there is an
					// object that must be loaded from map. Use a load method.
					// 4. *cry*
					
					if (from == to) {
						
						setTo = fromData;
						
					} else if (ConverterRegistry.isConverter(from, to)) {
						
						setTo = ConverterRegistry.getConverter(from, to).convert(fromData);
						
					} else if (fromData instanceof Map<?, ?>
							&& !field.getType().isAssignableFrom(Map.class)) {
						
						try {
							setTo = field.getType().newInstance();
						} catch (Exception e) {
							throw new ConfigurationException.ReflectionException(
									"Couldn't create an object of " + field.getType()
											+ ", as there is no empty constructor.",
									e);
						}
						
						load(setTo.getClass(), setTo, (Map) fromData);
						
					} else {
						
						throw new ConfigurationException.LoadingException(
								"No way to convert " + from + " -> " + to);
						
					}
					
				}
				
				// Try to apply custom loader, if necessary
				
				try {
					
					if (loaderInfo.hasCustomLoader())
						loaderInfo.customLoaderClass.newInstance().load(null, setTo);
					
				} catch (InstantiationException | IllegalAccessException e) {
					
					throw new ConfigurationException.ReflectionException(
							"Couldn't create a loader class of loader "
									+ loaderInfo.customLoaderClass.getName(),
							e);
					
				} catch (Exception e) {
					
					throw new ConfigurationException.Unexpected(
							"An unexpected error occurred while using a custom object loader from config. Offending loader is: "
									+ loaderInfo.customLoaderClass,
							e);
					
				}
				
				if (loaderInfo.loadFields) field.set(obj, setTo);
				
			}
			
		} catch (ConfigurationException e) {
			
			throw e;
			
		} catch (Exception e) {
			
			throw new ConfigurationException.Unexpected("An unexpected error occurred while loading field \""
					+ field.getName() + "\" in class \"" + cls.getName() + "\"", e);
			
		}
		
	}
	
	private void save() {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("config/" + path)));
			
			DumperOptions options = new DumperOptions();
			options.setPrettyFlow(true);
			options.setDefaultFlowStyle(FlowStyle.BLOCK);
			
			String asText = "";
			Yaml yaml = new Yaml(options);
			asText = yaml.dump(values);
			
			writer.write(asText);
			writer.close();
			
		} catch (IOException e) {
			
			throw new ConfigurationException.LoadingException("Exception while trying to save config file",
					e);
			
		}
		
	}
	
	// TODO Implement
	/**
	 * Return a yaml representation of that object.
	 */
	public static String yaml(Object obj) {
		return null;
	}
	
	/**
	 * Keeps track of a custom loader
	 * 
	 * @author CrowsOfWar
	 */
	private static class CustomLoaderSettings {
		
		private final Class<? extends CustomObjectLoader> customLoaderClass;
		private final boolean loadFields;
		
		/**
		 * Create a custom loader info, where there is the defaults
		 */
		private CustomLoaderSettings() {
			this.customLoaderClass = null;
			this.loadFields = true;
		}
		
		/**
		 * Create a custom loader info using data from the annotation
		 */
		private CustomLoaderSettings(HasCustomLoader annot) {
			this.customLoaderClass = annot.loaderClass();
			this.loadFields = annot.loadMarkedFields();
		}
		
		private boolean hasCustomLoader() {
			return customLoaderClass != null;
		}
		
	}
	
}
