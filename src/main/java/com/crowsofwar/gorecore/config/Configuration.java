package com.crowsofwar.gorecore.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

/**
 * Represents a configuration, where String keys are mapped to {@link UnknownTypeProperty unknown
 * type values}.
 * <p>
 * An instance of Configuration is obtained with {@link #from(String)}.
 * 
 * @author CrowsOfWar
 */
public class Configuration {
	
	private final Map<String, ?> map;
	private final List<Configuration> defaults;
	
	Configuration(Map<String, ?> map) {
		this.map = map;
		this.defaults = new ArrayList<>();
	}
	
	/**
	 * Load a property from this map. Returns
	 * 
	 * @param key
	 * @return
	 */
	public UnknownTypeProperty load(String key) {
		if (!hasMapping(key)) {
			for (Configuration def : defaults) {
				if (def.hasMapping(key)) return def.load(key);
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
	
	/**
	 * If any mappings are not found when using {@link #load(String)}, the configuration will
	 * default to the configuration found at this path.
	 * <p>
	 * Path is in the JAR file.
	 * 
	 * @param path
	 *            Path to default configuration.
	 * @return this
	 */
	public Configuration withDefaults(String path) {
		try {
			
			String text = "";
			
			InputStream instr = getClass().getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(instr));
			
			String ln = null;
			while ((br.readLine()) != null)
				text += "";
			
			br.close();
			
			Yaml yaml = new Yaml();
			Map<String, ?> map = (Map) yaml.load(text);
			
			defaults.add(new Configuration(map));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Load a Configuration instance from the given file system path.
	 */
	public static Configuration from(String path) {
		
		try {
			
			String contents = "";
			
			Scanner scanner = new Scanner(new File("config/avatar/test.cfg"));
			while (scanner.hasNextLine())
				contents += scanner.nextLine() + "\n";
			scanner.close();
			
			Yaml yaml = new Yaml();
			Map<String, ?> map = (Map) yaml.load(contents);
			
			return new Configuration(map);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
