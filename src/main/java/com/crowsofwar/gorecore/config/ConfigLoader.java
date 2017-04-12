/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.Level;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.config.convert.ConverterRegistry;

/**
 * A configuration loader. It populates the fields of an object, with data from
 * disk, using reflection.
 * <p>
 * Each configuration loader may, in turn, create more objects which need to be
 * loaded- a field may have a custom type.
 * <p>
 * Load an object by using {@link #load(Object, String)}.
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
	
	/**
	 * A list of class tags added to the representer, for debugging if something
	 * goes wrong.
	 */
	private final List<Class<?>> classTags;
	
	/**
	 * Config files typically have a key called IGNORE_CONFIG_FILE. This is used
	 * so defaults changed between updates will not get overriden by any old
	 * config files.
	 */
	private final boolean ignoreConfigFile;
	
	private ConfigLoader(String path, Object obj, Map<String, ?> data, boolean ignoreConfigFile) {
		this.path = path;
		this.obj = obj;
		this.data = data == null ? new HashMap<>() : data;
		this.usedValues = new HashMap<>();
		this.representer = new Representer();
		this.classTags = new ArrayList<>();
		this.ignoreConfigFile = ignoreConfigFile;
	}
	
	/**
	 * Populate the {@link #obj object's} data with the information from the
	 * {@link #data map}, converting as necessary. Will also add any used values
	 * to {@link #usedValues}.
	 * 
	 * Not to be confused with {@link #load(Object, String)}, which creates a
	 * ConfigLoader then calls load on it.
	 */
	private void load() {
		
		// TODO Load declared fields of the superclass as well
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			
			field.setAccessible(true);
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
				
				if (Modifier.isStatic(field.getModifiers())) {
					
					GoreCore.LOGGER.log(Level.WARN,
							"[ConfigLoader] Warning: Not recommended to mark static fields with @Load, may work out weirdly.");
					GoreCore.LOGGER.log(Level.WARN,
							"This field is " + field.getDeclaringClass().getName() + "#" + field.getName());
					GoreCore.LOGGER.log(Level.WARN, "Use a singleton instead!");
					
				}
				
				// Should load this field
				
				HasCustomLoader loaderAnnot = fieldType.getAnnotation(HasCustomLoader.class);
				CustomLoaderSettings loaderInfo = loaderAnnot == null ? new CustomLoaderSettings()
						: new CustomLoaderSettings(loaderAnnot);
				
				Object fromData = data.get(field.getName());
				Object setTo;
				
				boolean tryDefaultValue = fromData == null || ignoreConfigFile;
				
				if (tryDefaultValue) {
					
					// Nothing present- try to load default value
					
					if (field.get(obj) != null) {
						
						setTo = field.get(obj);
						
					} else {
						throw new ConfigurationException.UserMistake(
								"No configured definition for " + field.getName() + ", no default value");
					}
					
				} else {
					
					// Value present in configuration.
					// Use the present value from map: fromData
					
					Class<Object> from = (Class<Object>) fromData.getClass();
					Class<?> to = fieldType;
					
					setTo = convert(fromData, to, field.getName());
					
				}
				usedValues.put(field.getName(), setTo);
				
				// If not a java class, probably custom; needs to NOT have the
				// '!!' in front
				if (!setTo.getClass().getName().startsWith("java")) {
					representer.addClassTag(setTo.getClass(), Tag.MAP);
					classTags.add(setTo.getClass());
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
			
			return (T) object;
			
		} else if (from.isAssignableFrom(to) || to.isAssignableFrom(from)) {
			
			return (T) object;
			
		} else if (ConverterRegistry.isConverter(from, to)) {
			
			return ConverterRegistry.getConverter(from, to).convert(object);
			
		} else if (object instanceof Map<?, ?> && !to.isAssignableFrom(Map.class)) {
			
			T loadedObject;
			
			try {
				loadedObject = to.newInstance();
				
				ConfigLoader loader = new ConfigLoader(path, loadedObject, (Map) data.get(name),
						this.ignoreConfigFile);
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
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		Yaml yaml = new Yaml(representer, options);
		
		try {
			
			Map<String, Object> sorted = new TreeMap<>(usedValues);
			return yaml.dump(sorted);
			
		} catch (YAMLException e) {
			throw new ConfigurationException.Unexpected(
					"Unexpected error while trying to convert values to YAML: classTags " + classTags
							+ ", values " + usedValues,
					e);
		}
		
	}
	
	private void save() {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("config/" + path)));
			
			String write = "";
			if (ignoreConfigFile) {
				write += "# WARNING : Any changes to this config file will not take effect!!\n";
				write += "# To fix this, set 'IGNORE_CONFIG_FILE: true' --> 'IGNORE_CONFIG_FILE: false'\n";
				write += "# This was done to prevent default values in new versions from being overriden\n";
				write += "# by outdated config files. By doing this, you will no longer recieve any new\n";
				write += "# config defaults...\n\n";
			}
			write += "IGNORE_CONFIG_FILE: " + ignoreConfigFile + "\n\n";
			write += dump();
			write = write.replace("\n", System.getProperty("line.separator"));
			
			writer.write(write);
			writer.close();
			
		} catch (IOException e) {
			
			throw new ConfigurationException.LoadingException("Exception while trying to save config file",
					e);
			
		}
		
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
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, Object> map = (Map) yaml.load(contents);
			
			return map == null ? new HashMap<>() : map;
			
		} catch (IOException e) {
			throw new ConfigurationException.LoadingException(
					"Exception trying to load config file at " + new File("config/" + path).getAbsolutePath(),
					e);
		} catch (ClassCastException e) {
			
			System.out
					.println("ConfigLoader- warning: File at " + path + " was not a map; ignored contents.");
			return new HashMap<>();
			
		} catch (ScannerException e) {
			
			throw new ConfigurationException.LoadingException("Malformed YAML file at config/" + path, e);
			
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
	 * be preserved if there is no entry in the configuration file.
	 * <p>
	 * To specify default values, simply set their current value. If the value
	 * of the field is <code>null</code> when this method is called, there MUST
	 * be an entry in configuration.
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
		Map<String, Object> map = loadMap(path);
		
		// Determine whether IGNORE_CONFIG_FILE is true or false
		Object ignoreObject = map.get("IGNORE_CONFIG_FILE");
		boolean ignoreSetting;
		if (ignoreObject == null || !(ignoreObject instanceof Boolean)) {
			ignoreSetting = true;
		} else {
			ignoreSetting = (boolean) ignoreObject;
		}
		
		ConfigLoader loader = new ConfigLoader(path, obj, map, ignoreSetting);
		loader.load();
		loader.save();
	}
	
	public static void save(Object obj, String path) {
		try {
			
			Map<String, Object> map = new HashMap<>();
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(Load.class) != null) {
					field.setAccessible(true);
					map.put(field.getName(), field.get(obj));
				}
			}
			
			ConfigLoader loader = new ConfigLoader(path, obj, map, false);
			loader.usedValues.putAll(map);
			loader.save();
			
		} catch (Exception e) {
			GoreCore.LOGGER.error("Error saving config @ " + path, e);
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
