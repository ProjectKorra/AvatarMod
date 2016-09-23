package com.crowsofwar.gorecore.config;

import java.io.File;
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
	
	Configuration(Map<String, ?> map) {
		this.map = map;
	}
	
	/**
	 * Load a property from this map. Returns
	 * 
	 * @param key
	 * @return
	 */
	public UnknownTypeProperty load(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new UnknownTypeProperty(key, map.get(key));
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
	 * The following would return commandBlock:
	 * <code>config.fromMapping("commandSettings").load("commandBlock").asString()</code> The
	 * following would return commandSettings:
	 * <code>config.load("commandSettings").asMapping()</code>
	 * 
	 * @param key
	 *            String key to fetch value from
	 */
	public Configuration fromMapping(String key) {
		if (!map.containsKey(key)) throw new IllegalArgumentException("Invalid key: " + key);
		return new Configuration((Map) map.get(key));
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
