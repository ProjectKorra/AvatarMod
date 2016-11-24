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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

/**
 * Represents a configuration, where String keys are mapped to
 * {@link UnknownTypeProperty unknown type values}.
 * <p>
 * An instance of Configuration is obtained with {@link #from(String)}.
 * 
 * @author CrowsOfWar
 */
public class Configuration {
	
	private Map<String, Object> map;
	private List<Configuration> defaults;
	private String path;
	private List<Mapping> missingMappings;
	
	Configuration(Object obj) {
		this.defaults = new ArrayList<>();
		this.missingMappings = new ArrayList<>();
		if (obj == null) {
			this.map = new HashMap<>();
		} else if (obj instanceof Map) {
			construct((Map) obj);
		} else {
			throw new ConfigException("Config file is not a map");
		}
	}
	
	private void construct(Map<String, Object> map) {
		this.map = map;
		System.out.println("Set map to " + map);
		this.defaults = new ArrayList<>();
	}
	
	/**
	 * Load a property from this map. If the property is not defined in this
	 * configuration, defers to other configurations to see if they have a
	 * mapping. If this configuration and the defaults don't have a mapping,
	 * throws an IllegalArgumentException.
	 * 
	 * @param key
	 *            String key to load
	 */
	public UnknownTypeProperty load(String key) {
		if (!hasMapping(key)) {
			for (Configuration def : defaults) {
				if (def.hasMapping(key)) {
					UnknownTypeProperty property = def.load(key);
					missingMappings.add(new Mapping(key, property.getObject()));
					return property;
				}
			}
			throw new IllegalArgumentException("Invalid key: " + key);
		}
		return new UnknownTypeProperty(key, map.get(key));
	}
	
	public boolean hasMapping(String key) {
		return map.containsKey(key);
	}
	
	/**
	 * Return a configuration instance from a mapping.
	 * <p>
	 * 
	 * <pre>
	 * spellSize: 4
	 * commandSettings:
	 *   commandBlock: true
	 *   configurationCommand: true
	 * </pre>
	 * 
	 * The following would return commandBlock:<br />
	 * <code>config.fromMapping("commandSettings").load("commandBlock").asString()</code>
	 * <p>
	 * The following would return commandSettings:<br />
	 * <code>config.load("commandSettings").asMapping()</code>
	 * 
	 * @param key
	 *            String key to fetch value from
	 */
	public Configuration fromMapping(String key) {
		return load(key).asMapping();
	}
	
	public Configuration set(String key, Object val) {
		map.put(key, val);
		return this;
	}
	
	public void save() throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		
		DumperOptions options = new DumperOptions();
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		Map<String, Object> toDump = new HashMap<>(map);
		for (Mapping mapping : missingMappings) {
			toDump.put(mapping.key, mapping.value);
		}
		
		String asText = "";
		Yaml yaml = new Yaml(options);
		asText = yaml.dump(toDump);
		
		writer.write(asText);
		writer.close();
		
	}
	
	/**
	 * If any mappings are not found when using {@link #load(String)}, the
	 * configuration will defer to the configuration found at this path.
	 * <p>
	 * Path is in the JAR file, relative to src/main/resources. If file does not
	 * exist, throws an exception.
	 * 
	 * @param path
	 *            Path to default configuration.
	 * @return this
	 */
	public Configuration withDefaults(String path) {
		try {
			
			String text = "";
			
			InputStream instr = getClass().getClassLoader().getResourceAsStream(path);
			if (instr == null)
				throw new FileNotFoundException("Default configuration file not found: " + path);
			BufferedReader br = new BufferedReader(new InputStreamReader(instr, "UTF-8"));
			
			String ln = null;
			while ((ln = br.readLine()) != null)
				text += ln + "\n";
			
			br.close();
			
			Yaml yaml = new Yaml();
			Object loaded = yaml.load(text);
			
			if (loaded != null) {
				defaults.add(new Configuration(loaded));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Load a Configuration instance from the given file system path.
	 * <p>
	 * If the file at that location was not found, automatically creates it.
	 */
	public static Configuration from(String path) {
		
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
			
			Configuration config = new Configuration(map);
			config.path = "config/" + path;
			return config;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	static class Mapping {
		
		private final String key;
		private final Object value;
		
		public Mapping(String key, Object value) {
			this.key = key;
			this.value = value;
		}
		
	}
	
}
