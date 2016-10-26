package com.crowsofwar.gorecore.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.ClassUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.crowsofwar.gorecore.config.convert.ConverterRegistry;

/**
 * Each ConfigLoader is responsible for populating fields of an object with
 * configuration values
 * 
 * @author CrowsOfWar
 */
public class ConfigLoader {
	
	/**
	 * Path to configuration file
	 */
	private final String path;
	
	/**
	 * The data read from config. May need conversion.
	 */
	private final Map<String, ?> data;
	
	/**
	 * The object which needs its fields populated
	 */
	private final Object obj;
	
	/**
	 * A map of the values which were used
	 */
	private final Map<String, Object> usedValues;
	
	private final Representer representer;
	
	private ConfigLoader(String path, Object obj, Map<String, ?> data) {
		this.path = path;
		this.obj = obj;
		this.data = data == null ? new HashMap<>() : data;
		this.usedValues = new HashMap<>();
		this.representer = new Representer();
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
			
			System.out
					.println("ConfigLoader- warning: File at " + path + " was not a map; ignored contents.");
			return new HashMap<>();
			
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
		ConfigLoader loader = new ConfigLoader(path, obj, loadMap(path));
		loader.load();
		loader.save();
	}
	
	/**
	 * Populate the {@link #obj object's} data with the information from the
	 * {@link #data map}, converting as necessary. Will also add any used values
	 * to {@link #usedValues}.
	 */
	private void load() {
		
		// TODO Load declared fields of the superclass as well
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			
			loadField(field);
			
		}
		
	}
	
	/**
	 * Tries to load the field of the {@link #obj object} with the correct
	 * {@link #data}.
	 * <p>
	 * If the field isn't marked with @Load, does nothing. Otherwise, will
	 * attempt to set the field's value (with reflection) to the data set in the
	 * map.
	 * 
	 * @param field
	 *            The field to load
	 */
	private <T> void loadField(Field field) {
		
		Class<?> cls = field.getDeclaringClass();
		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive()) fieldType = ClassUtils.primitiveToWrapper(fieldType);
		
		try {
			
			if (field.getAnnotation(Load.class) != null) {
				System.out.println("Should load " + field.getName());
				// Should load this field
				
				HasCustomLoader loaderAnnot = fieldType.getAnnotation(HasCustomLoader.class);
				CustomLoaderSettings loaderInfo = loaderAnnot == null ? new CustomLoaderSettings()
						: new CustomLoaderSettings(loaderAnnot);
				
				Object fromData = data.get(field.getName());
				Object setTo;
				
				if (fromData == null) {
					
					// Nothing present- try to load default value
					System.out.println(" -> Nothing present; trying default value");
					
					if (field.get(obj) != null) {
						
						setTo = field.get(obj);
						
						System.out.println(" -> Found default " + setTo);
						
					} else {
						throw new ConfigurationException.UserMistake(
								"No configured definition for " + field.getName() + ", no default value");
					}
					
				} else {
					
					// Value present in configuration.
					// Use the present value from map: fromData
					
					Class<Object> from = (Class<Object>) fromData.getClass();
					Class<?> to = fieldType;
					
					System.out.println(" -> Using from cfg.");
					System.out.println(" -> Convert " + from + "-> " + to);
					
					setTo = convert(fromData, to, field.getName());
					
				}
				usedValues.put(field.getName(), setTo);
				
				// If not a java class, probably custom; needs to NOT have the
				// '!!' in front
				if (!setTo.getClass().getName().startsWith("java")) {
					representer.addClassTag(setTo.getClass(), Tag.MAP);
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
	
	/**
	 * Attempt to convert one type to another.
	 * <p>
	 * There are 4 possible trials of loading the class...
	 * <p>
	 * <ol>
	 * <li>The object's class is <code>to</code> already- no conversions needed.
	 * So just return the object</li>
	 * <li>The object's class is an instance of <code>to</code>, so just return
	 * the object</li>
	 * <li>A converter was found, to convert the object's type into the desired
	 * type. See {@link ConverterRegistry}.</li>
	 * <li>As a last resort, if the object is a Map, that means that it probably
	 * represents data for a class. Will instantiate an instance of
	 * <code>to</code> with reflection, then create a ConfigLoader and load it.
	 * </li>
	 * </ol>
	 * 
	 * @param object
	 *            The object to convert
	 * @param to
	 *            The type to convert to
	 * @param name
	 *            If there are no converters, a new object must be created
	 *            and @Load fields are populated. Will then use map from
	 *            {@link #data} with that name.
	 * 
	 * @param <T>
	 *            The type which we must convert to
	 */
	private <T> T convert(Object object, Class<T> to, String name) {
		
		// 4 possibilities. Done in this order:
		//
		// 1. from == to. So it is EXACTLY the right type already
		// 2. from is instance of to (or vice versa), so no
		// conversion is necessary
		// 3. There is a converter to convert from->to.
		// 4. from is a map. to is not. This means, there is an
		// object that must be loaded from map. Use a load method.
		// 5. cry
		
		Class<Object> from = (Class<Object>) object.getClass();
		
		if (from == to) {
			
			System.out.println(" -> Is already in the type we want.");
			return (T) object;
			
		} else if (from.isAssignableFrom(to) || to.isAssignableFrom(from)) {
			
			System.out.println(" -> Is a form of the type we want.");
			return (T) object;
			
		} else if (ConverterRegistry.isConverter(from, to)) {
			
			System.out.println(" -> Used a converter.");
			return ConverterRegistry.getConverter(from, to).convert(object);
			
		} else if (object instanceof Map<?, ?> && !to.isAssignableFrom(Map.class)) {
			
			System.out.println(" -> Populating fields with reflection");
			
			T loadedObject;
			
			try {
				loadedObject = to.newInstance();
				
				ConfigLoader loader = new ConfigLoader(path, loadedObject, (Map) data.get(name));
				loader.load();
				usedValues.put(name, loader.dump());
				
			} catch (Exception e) {
				throw new ConfigurationException.ReflectionException(
						"Couldn't create an object of " + to + " with reflection", e);
			}
			
			return loadedObject;
			
		} else {
			
			throw new ConfigurationException.LoadingException("No way to convert " + from + " -> " + to);
			
		}
	}
	
	/**
	 * Dumps all used values from {@link #load()} into a YAML string.
	 */
	private String dump() {
		DumperOptions options = new DumperOptions();
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		Yaml yaml = new Yaml(representer, options);
		
		return yaml.dump(usedValues);
		
	}
	
	private void save() {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("config/" + path)));
			
			writer.write(dump());
			writer.close();
			
		} catch (IOException e) {
			
			throw new ConfigurationException.LoadingException("Exception while trying to save config file",
					e);
			
		}
		
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
